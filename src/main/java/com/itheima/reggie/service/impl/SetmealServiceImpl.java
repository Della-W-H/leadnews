package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.Dto.SetmealDto;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

  @Autowired
  private SetmealMapper setmealMapper;
  @Autowired
  private SetmealDishMapper setmealDishMapper;
  @Autowired
  private CategoryMapper categoryMapper;
  @Autowired
  private DishMapper dishMapper;

  @Override
  public Result add(SetmealDto setmealDto) {
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDto,setmeal);
    setmealMapper.insert(setmeal);

    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
    for (SetmealDish setmealDish : setmealDishes) {
      setmealDish.setSetmealId(setmeal.getId());
      setmealDishMapper.insert(setmealDish);
    }
    return Result.success("添加成功");
  }

  @Override
  public Result page(Integer page, Integer pageSize, String name) {
    IPage<Setmeal> setPage = new Page<>(page,pageSize);

    IPage<Setmeal> setmealIPage;
    if(null != name){
      LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
      wrapper.like(Setmeal::getName,name);
      setmealIPage = setmealMapper.selectPage(setPage, wrapper);
    } else {
      setmealIPage = setmealMapper.selectPage(setPage,null);
    }

    List<Setmeal> dishes = setmealIPage.getRecords();
    ArrayList<SetmealDto> setmealDtos;

    String jsonDishes = JSON.toJSONString(dishes);
    setmealDtos = (ArrayList<SetmealDto>) JSON.parseArray(jsonDishes, SetmealDto.class);
    setmealDtos.forEach(c->{                            //foreach本质上是个迭代器，并不能改变原有的数据属性，一般而言只能做遍历使用，但对于引用型数据，因为其本质是个指针，可以做到对于地址内容的修改，故可以修改原有数据哈
      Category category = categoryMapper.selectById(c.getCategoryId());
      c.setCategoryName(category.getName());
    });
    IPage<SetmealDto> setmealDtoPage = new Page<>();
    BeanUtils.copyProperties(setmealDtos,setmealDtoPage);
    setmealDtoPage.setRecords(setmealDtos);

    return Result.success(setmealDtoPage);
  }

  @Override
  public Result deleteByIds(Long[] ids) {
    setmealMapper.deleteBatchIds(Arrays.asList(ids));
    LambdaUpdateWrapper<SetmealDish> wrapper = new LambdaUpdateWrapper<>();
    wrapper.in(SetmealDish::getSetmealId,ids);
    setmealDishMapper.delete(wrapper);
    return Result.success("删除成功");
  }

  @Override
  public Result getById(Long id) {
    SetmealDto setmealDto = new SetmealDto();
    Setmeal setmeal = setmealMapper.selectById(id);
    BeanUtils.copyProperties(setmeal,setmealDto);
    LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(SetmealDish::getSetmealId,id);
    List<SetmealDish> setmealDishes = setmealDishMapper.selectList(wrapper);
    setmealDto.setSetmealDishes(setmealDishes);
    return Result.success(setmealDto);
  }

  @Override
  public Result update(SetmealDto setmealDto) {

    setmealMapper.updateById(setmealDto);
    LambdaUpdateWrapper<SetmealDish> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
    setmealDishMapper.delete(wrapper);
    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
    for (SetmealDish setmealDish : setmealDishes) {
      setmealDish.setSetmealId(setmealDto.getId());
      setmealDishMapper.insert(setmealDish);
    }
    return Result.success("修改成功");
  }

  @Override
  public Result changeStatus(Long[] ids, Integer changeStatus) {
    LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
    wrapper.set(Setmeal::getStatus,changeStatus).in(Setmeal::getId,ids);
    setmealMapper.update(null,wrapper);
    return Result.success("修改成功");
  }

  @Override
  public Result findAll(Long categoryId, Integer status) {
    List<Dish> dishes = dishMapper
        .selectList(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, categoryId));
    List<Setmeal> setmeals = setmealMapper
        .selectList(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, categoryId));
    return Result.success(dishes.size()==0?setmeals:dishes);
  }
}
