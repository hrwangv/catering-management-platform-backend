package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;//处理订单表

    @Autowired
    private OrderDetailMapper orderDetailMapper;//处理订单明细表

    @Autowired
    private AddressBookMapper addressBookMapper;//用于判断地址是否为空

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;//用于判断购物车是否为空

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    //用户下单实现方法
    @Override
    @Transactional   //事务注解，为了数据的一致性，一起成功一起失败
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理各种业务异常：

        //通过当前订单的id获取正在操作的Addressbook对象,
        AddressBook addressBookById = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        //地址为空无法下单
        if(addressBookById == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车为空无法下单
        Long currentId = BaseContext.getCurrentId();//获得当前用户的id
        ShoppingCart shoppingCart = new ShoppingCart(); //构造向list方法传的完整的购物车实体对象
        shoppingCart.setUserId(currentId);//给要查的购物车对象赋id值
        //购物车实体对象中只有用户id有值，查出购物车
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList == null || shoppingCartList.isEmpty()) { //购物车为空不能下单
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单实体表插入1条下单的数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID); //未付款
        orders.setStatus(Orders.PENDING_PAYMENT); //待支付
        orders.setNumber(String.valueOf(System.currentTimeMillis())); //使用时间戳（先转化为字符串类型的）作为订单号
        orders.setPhone(addressBookById.getPhone()); //用户手机号
        orders.setConsignee(addressBookById.getConsignee()); //收货人
        orders.setUserId(currentId); //用户id

        orderMapper.insert(orders); //插入，同时返回订单id


        //创建一个订单明细表集合，集合中存放订单明细
        List<OrderDetail> orderDetailList = new ArrayList<>();

        //向订单明细表插入多条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();//订单明细对象
            BeanUtils.copyProperties(cart, orderDetail); //将购物车列表中的数据拷到对象属性中
            orderDetail.setOrderId(orders.getId()); //insert插入后返回主键值，设置订单明细额关联的订单id
            orderDetailList.add(orderDetail); //向订单明细表中依次插入订单明细
        }

        //批量插入订单明细表
        orderDetailMapper.insertBatch(orderDetailList);

        //下单后清空购物车数据
        shoppingCartMapper.deleteByUserId(currentId);

        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }



    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);//获取当前用户

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }


    //客户催单方法
    @Override
    public void reminder(Long id) {
        //先检测订单是否为存在
        Orders byId = orderMapper.getById(id);
        if (byId == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //通过websocket向客户端浏览器发送消息,json格式的数据 type currentId content
        Map map = new HashMap();
        map.put("type",2);//1表示来单提醒，2表示客户催单
        map.put("orderId", id);
        map.put("content", "订单号" + byId.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));

    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //通过websocket向客户端浏览器发送消息,json格式的数据 type currentId content
        //先把要传递的信息放到map中，再将map转化成json格式
        Map map = new HashMap();
        map.put("type",1);//1表示来单提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());  //要提示的订单id
        map.put("content", "订单号" +outTradeNo);  //消息内容

        String jsonString = JSON.toJSONString(map);//转json
        webSocketServer.sendToAllClient(jsonString); //发送消息
    }

}
