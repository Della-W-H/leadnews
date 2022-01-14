package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;
  @Autowired
  private HttpServletRequest request;

  @GetMapping("/userPage")
  public Result findByPage(int page,int pageSize){
    return orderService.findByPage(page,pageSize);
  }

  @PostMapping("/submit")
  public Result submit(@RequestBody Orders orders) throws Exception {
    Long userId = (Long) request.getSession().getAttribute("employee");
    orders.setUserId(userId);
    return orderService.submit(orders);
  }
}
