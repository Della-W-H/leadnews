package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commons.RedisContant;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryMapper mapper;
  @Autowired
  private DishMapper dishMapper;
  @Autowired
  private SetmealMapper setmealMapper;
  @Resource
  private RedisTemplate redisTemplate;

  @Override
  public Result add(Category category) {
    mapper.insert(category);
    redisTemplate.delete(RedisContant.CATEGORYALL);
    return Result.success("添加成功");
  }

  @Override
  public Result findByPage(Integer page, Integer pageSize) {
    IPage<Category> categoryIPage = new Page<>(page,pageSize);
    LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
    wrapper.orderByAsc(Category::getSort);
    categoryIPage = mapper.selectPage(categoryIPage, wrapper);
    return Result.success(categoryIPage);
  }

  @Override
  public Result deleteById(Long id) {
    LambdaQueryWrapper<Dish> disnWrapper = new LambdaQueryWrapper<>();
    disnWrapper.eq(Dish::getCategoryId,id);
    List<Dish> dishes = dishMapper.selectList(disnWrapper);
    if(dishes.size()>0){
      return Result.error("此分类下存在菜品不能删除");
    }
    LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
    setmealWrapper.eq(Setmeal::getCategoryId,id);
    List<Setmeal> setmeals = setmealMapper.selectList(setmealWrapper);
    if(setmeals.size()>0){
      return Result.error("此分类下存在套餐不能删除");
    }
    mapper.deleteById(id);
    redisTemplate.delete(RedisContant.CATEGORYALL);
    return Result.success("删除成功");
  }

  @Override
  public Result update(Category category) {
    mapper.updateById(category);
    redisTemplate.delete(RedisContant.CATEGORYALL);
    return Result.success("修改成功");
  }

  @Override
  public Result getByType(Integer type) {

    List<Category> list = (List<Category>) redisTemplate.opsForValue().get(RedisContant.CATEGORYALL);
    if (null == list || list.size() ==0) {
      if (null == type) {
        list = mapper.selectList(null);
      } else {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, type);
        list = mapper.selectList(wrapper);
      }
      redisTemplate.opsForValue().set(RedisContant.CATEGORYALL,list);
    }
    return Result.success(list);
  }

}
