package com.itheima.reggie.service;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.SetmealDto;

public interface SetmealService {

  Result add(SetmealDto setmealDto);

  Result page(Integer page, Integer pageSize, String name);

  Result deleteByIds(Long[] ids);

  Result getById(Long id);

  Result update(SetmealDto setmealDto);

  Result changeStatus(Long[] ids, Integer changeStatus);

  Result findAll(Long categoryId, Integer status);
}
