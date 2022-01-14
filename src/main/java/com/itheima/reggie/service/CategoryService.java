package com.itheima.reggie.service;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Category;

public interface CategoryService {

  Result add(Category category);

  Result findByPage(Integer page, Integer pageSize);

  Result deleteById(Long id);

  Result update(Category category);

  Result getByType(Integer type);
}
