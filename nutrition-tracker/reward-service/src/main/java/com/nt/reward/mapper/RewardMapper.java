package com.nt.reward.mapper;

import com.nt.reward.domain.po.Gift;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RewardMapper {

    /**
     * 根据ID查询礼品
     */
    @Select("SELECT * FROM gift WHERE id = #{id}")
    Gift selectById(Long id);

    /**
     * 扣减库存
     * 只有当库存足够时才会更新成功
     */
    @Update("UPDATE gift SET stock = stock - #{count} " +
            "WHERE id = #{id} AND stock >= #{count}")
    int deductStock(Long id, Integer count);

    /**
     * 插入兑换订单
     */
    @Insert("INSERT INTO reward_order(user_id, gift_id, count, total_points, status) " +
            "VALUES(#{userId}, #{giftId}, #{count}, #{totalPoints}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(com.nt.reward.entity.RewardOrder order);

    /**
     * 查询所有礼品
     */
    @Select("SELECT id, name, description, image_url, required_points, stock, status, created_at, updated_at " +
            "FROM gift " +
            "WHERE stock > 0 AND status = 1")
    List<Gift> listGifts();
}
