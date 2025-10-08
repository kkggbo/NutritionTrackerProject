package com.nt.reward.service.impl;

import com.nt.common.Result;
import com.nt.common.utils.UserThreadLocal;
import com.nt.reward.client.UserClient;
import com.nt.reward.domain.dto.ExchangeRequestDTO;
import com.nt.reward.domain.po.Gift;
import com.nt.reward.mapper.RewardMapper;
import com.nt.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardMapper rewardMapper;

    private final UserClient userClient;

    @Override
    @GlobalTransactional(name = "exchange-gift-transaction", rollbackFor = Exception.class)
    @Transactional
    public Result<String> exchange(ExchangeRequestDTO request) {
        // 获取用户id、礼品id和兑换数量
        Long userId = UserThreadLocal.getUserId();
        Long giftId = request.getGiftId();
        int count = request.getCount();

        // 查询礼品数量
        Gift gift = rewardMapper.selectById(giftId);
        if (gift == null || gift.getStatus() == 0) {
            throw new RuntimeException("礼品不存在或已下架");
        }
        if (gift.getStock() < count) {
            throw new RuntimeException("库存不足");
        }

        int totalPoints = gift.getRequiredPoints() * count;

        // 调用 user-service 扣除积分（分布式事务参与者）
        boolean success = userClient.deductPoints(userId, totalPoints);
        if (!success) {
            throw new RuntimeException("扣减积分失败");  // 触发全局回滚
        }

        // 扣减库存
        int updated = rewardMapper.deductStock(giftId, count);
        if (updated == 0) {
            throw new RuntimeException("扣减库存失败"); // 触发全局回滚
        }

        // 创建订单
        com.nt.reward.entity.RewardOrder order = new com.nt.reward.entity.RewardOrder();
        order.setUserId(userId);
        order.setGiftId(giftId);
        order.setCount(count);
        order.setTotalPoints(totalPoints);
        order.setStatus(0); // 待处理
        int res = rewardMapper.insertOrder(order);

        if (res == 0) {
            throw new RuntimeException("创建订单失败"); // 触发全局回滚
        }

        return Result.success("兑换成功");
    }

    @Override
    public Result<List<Gift>> listGifts() {
        List<Gift> gifts = rewardMapper.listGifts();
        return Result.success(gifts);
    }

}

