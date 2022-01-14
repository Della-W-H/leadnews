package com.itheima.reggie.service;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Orders;

public interface OrderService {

  Result findByPage(int page, int pageSize);

  Result submit(Orders orders) throws Exception;

}
