package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderPaymentVO;
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

    //支付成功修改订单状态
    void paySuccess(String outTradeNo);

    //支付成功
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
}
