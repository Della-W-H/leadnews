package com.itheima.reggie.service;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.DaS;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService {

  Result getList(Long id);

  Result add(ShoppingCart shoppingCart);

  Result deleteById(Long id);

  Result subById(DaS daS, Long id);
}
