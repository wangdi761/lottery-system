package com.example.lottery.controller;

import com.example.lottery.dto.DrawRequest;
import com.example.lottery.dto.DrawResponse;
import com.example.lottery.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lottery")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @PostMapping("/draw")
    public DrawResponse draw(@RequestBody DrawRequest request) {
        return lotteryService.draw(request);
    }
}
