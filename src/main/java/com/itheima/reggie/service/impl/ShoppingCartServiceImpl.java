package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.DaS;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

  @Autowired
  private ShoppingCartMapper shoppingCartMapper;

  @Override
  public Result getList(Long id) {
    List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,id));
    return Result.success(shoppingCarts);
  }

  @Override
  public Result add(ShoppingCart shoppingCart) {
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getName,shoppingCart.getName()).eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor()).eq(ShoppingCart::getUserId,shoppingCart.getUserId());
    ShoppingCart one = shoppingCartMapper.selectOne(wrapper);
    if (!(one == null)){
      one.setNumber(one.getNumber()+1);
      shoppingCartMapper.updateById(one);
    } else {
      shoppingCartMapper.insert(shoppingCart);
    }
    return Result.success("添加成功");
  }

  @Override
  public Result deleteById(Long id) {
    shoppingCartMapper.delete(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,id));
    return Result.success("删除成功");
  }

  @Override
  public Result subById(DaS daS, Long id) {
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(daS.getDishId()!=null,ShoppingCart::getDishId,daS.getDishId());
    wrapper.eq(daS.getSetmealId()!=null,ShoppingCart::getSetmealId,daS.getSetmealId());
    wrapper.eq(daS.getDishFlavor()!=null,ShoppingCart::getDishFlavor,daS.getDishFlavor());
    wrapper.eq(ShoppingCart::getUserId,id);
    ShoppingCart shoppingCart = shoppingCartMapper.selectOne(wrapper);
    if (shoppingCart.getNumber()==1){
      shoppingCartMapper.deleteById(shoppingCart.getId());
    } else {
      shoppingCart.setNumber(shoppingCart.getNumber() - 1);
      shoppingCartMapper.update(shoppingCart, wrapper);
    }
    return Result.success("修改成功");
  }
}
