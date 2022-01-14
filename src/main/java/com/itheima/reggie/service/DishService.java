package com.itheima.reggie.service;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.DishDto;

public interface DishService {

  Result add(DishDto dishDto);

  Result getDishByPage(Integer page, Integer pageSize, String name);

  Result getDishById(String id);

  Result update(DishDto dishDto);

  Result deleteByIds(Long[] ids);

  Result motify(Integer status, Long[] ids);

  Result getListByCategoryId(Long categoryId);
}
