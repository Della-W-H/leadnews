package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.MyConfig;
import com.github.wxpay.sdk.WXPay;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.mapper.OrderDetailMapper;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.ShoppingCartService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private AddressBookMapper addressBookMapper;
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private OrderDetailMapper orderDetailMapper;
  @Autowired
  private ShoppingCartService shoppingCartService;

  @Override
  public Result findByPage(int page, int pageSize) {
    IPage<Orders> setPage = new Page<>(page,pageSize);
    IPage<Orders> ordersIPage = orderMapper.selectPage(setPage, null);
    return Result.success(ordersIPage);
  }

  @Override
  public Result submit(Orders orders) {

    AddressBook addressBook = addressBookMapper.selectOne(
        new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, orders.getUserId()));

    List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(
        new LambdaQueryWrapper<ShoppingCart>()
            .eq(ShoppingCart::getUserId, addressBook.getUserId()));

    BigDecimal total = BigDecimal.valueOf(0);
    for (ShoppingCart cart : shoppingCarts) {
      BigDecimal amount  = cart.getAmount().multiply(BigDecimal.valueOf(cart.getNumber()));
      total.add(amount);
    }

    String address = ""+addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
    orders.setAddress(address);
    orders.setCheckoutTime(LocalDateTime.now());
    orders.setConsignee(addressBook.getConsignee());
    orders.setStatus(3);
    orders.setOrderTime(LocalDateTime.now());
    orders.setPhone(addressBook.getPhone());
    orders.setUserId(addressBook.getUserId());
    orders.setAmount(total);
    orders.setNumber(UUID.randomUUID().toString());
    orderMapper.insert(orders);

    for (ShoppingCart cart : shoppingCarts) {
      String jsonString = JSON.toJSONString(cart);
      OrderDetail orderDetail = JSON.parseObject(jsonString, OrderDetail.class);
      orderDetail.setOrderId(orders.getId());
      orderDetailMapper.insert(orderDetail);
    }

    shoppingCartService.deleteById(addressBook.getUserId());

    //todo 请求微信获得支付的二维码信息
    MyConfig myConfig = new MyConfig();
    WXPay wxPay = null;
    try {
      wxPay = new WXPay(myConfig);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Map<String, String> requsetMap = new HashMap<>();
    requsetMap.put("body", "红烧肉盖浇饭");
    //商家的订单号
    requsetMap.put("out_trade_no",orders.getId()+"");
    //付款金额
    requsetMap.put("total_fee","1");//以分为单位

    requsetMap.put("spbill_create_ip","127.0.0.1");
    //回调通知  商家接收微信的信息的接口
    requsetMap.put("notify_url","http://");
    //支付类型
    requsetMap.put("trade_type","NATIVE");

    //模拟浏览器发送一个http请求
    /**
     * requsetMap 代表请求参数
     * responseMap  返回值
     */
    try {
      Map<String, String> map = wxPay.unifiedOrder(requsetMap);
      return Result.success(map);
    } catch (Exception e) {
      e.printStackTrace();
      return Result.error("支付失败哎");
    }


    //return Result.success("下单成功");
  }
}
