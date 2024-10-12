package com.example.lottery.service.impl;

import com.example.lottery.dto.DrawRequest;
import com.example.lottery.dto.DrawResponse;
import com.example.lottery.entity.DrawRecord;
import com.example.lottery.entity.Prize;
import com.example.lottery.repository.DrawChanceRepository;
import com.example.lottery.repository.DrawRecordRepository;
import com.example.lottery.repository.PrizeRepository;
import com.example.lottery.service.LotteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class LotteryServiceImpl implements LotteryService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);

    @Autowired
    private DrawChanceRepository drawChanceRepository;

    @Autowired
    private PrizeRepository prizeRepository;

    @Autowired
    private DrawRecordRepository drawRecordRepository;

    @Transactional
    @Override
    public DrawResponse draw(DrawRequest request) {
        logger.info("开始处理抽奖请求: userId={}, activityId={}", request.getUserId(), request.getActivityId());

        // 原子操作减少抽奖次数
        int updatedRows = drawChanceRepository.decrementDrawChance(request.getUserId(), request.getActivityId());
        if (updatedRows == 0) {
            logger.warn("用户没有抽奖机会或更新失败: userId={}, activityId={}", request.getUserId(), request.getActivityId());
            throw new RuntimeException("没有抽奖机会或更新失败");
        }

        logger.info("更新用户抽奖机会: userId={}, activityId={}", request.getUserId(), request.getActivityId());

        // 抽奖逻辑
        List<Prize> prizes = prizeRepository.findByActivityId(request.getActivityId());
        Prize prize = getPrize(prizes);

        // 记录抽奖结果
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setUserId(request.getUserId());
        drawRecord.setActivityId(request.getActivityId());
        drawRecord.setPrizeId(prize.getId());
        drawRecord.setDrawTime(LocalDateTime.now());
        drawRecordRepository.save(drawRecord);
        logger.info("记录抽奖结果: userId={}, activityId={}, prizeId={}", request.getUserId(), request.getActivityId(), prize.getId());

        // 返回抽奖结果
        DrawResponse response = new DrawResponse();
        response.setPrizeName(prize.getName());
        logger.info("抽奖完成: userId={}, activityId={}, prizeName={}", request.getUserId(), request.getActivityId(), prize.getName());
        return response;
    }

    private Prize getPrize(List<Prize> prizes) {
        prizes.sort(Comparator.comparingDouble(Prize::getProbability).reversed());
        Random random = new Random();
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (Prize prize : prizes) {
            // 概率小于等于0的奖品不参与抽奖
            if (prize.getProbability() <= 0) {
                continue;
            }
            cumulativeProbability += prize.getProbability();
            if (rand <= cumulativeProbability) {
                // 原子操作减少库存
                int updatedRows = prizeRepository.decrementStock(prize.getId());
                if (updatedRows > 0) {
                    logger.info("抽中奖品: prizeId={}, prizeName={}, stock={}", prize.getId(), prize.getName(), prize.getStock() - 1);
                    return prize;
                } else {
                    // 库存减少失败，跳出遍历并查找兜底奖品
                    logger.warn("库存减少失败: prizeId={}, prizeName={}", prize.getId(), prize.getName());
                    break;
                }
            }
        }
        // 返回兜底奖品
        Prize defaultPrize = prizes.stream().filter(Prize::isDefault).findFirst().orElseThrow(() -> new RuntimeException("没有兜底奖品"));
        logger.info("抽中奖品: prizeId={}, prizeName={} (兜底奖品)", defaultPrize.getId(), defaultPrize.getName());
        return defaultPrize;
    }
}
