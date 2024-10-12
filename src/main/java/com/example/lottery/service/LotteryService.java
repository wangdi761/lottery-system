package com.example.lottery.service;

import com.example.lottery.dto.DrawRequest;
import com.example.lottery.dto.DrawResponse;

public interface LotteryService {
    DrawResponse draw(DrawRequest request);
}
