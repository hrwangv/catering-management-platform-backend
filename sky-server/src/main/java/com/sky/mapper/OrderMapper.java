package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    //根据订单状态和查询时间拆线呢订单信息
    @Select("select * from orders where status = #{status} and order_time < (#{orderTime})")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    //修改订单信息
    void update(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);


    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);




}
