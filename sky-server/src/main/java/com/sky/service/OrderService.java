package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
public interface OrderService {

    //用户下单接口
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    //客户催单
    void reminder(Long id);
}
