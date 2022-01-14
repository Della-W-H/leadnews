package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.commons.RedisContant;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Dto.DishDto;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DishServiceImpl implements DishService {

  @Resource
  private DishMapper dishMapper;
  @Resource
  private DishFlavorMapper dishFlavorMapper;
  @Resource
  private CategoryMapper categoryMapper;
  @Resource
  private RedisTemplate redisTemplate;

  @Override
  public Result add(DishDto dishDto) {
    if(dishDto == null){
      return Result.error("参数错误");
    }

    Dish dish = new Dish();
    //使用spring工具类复制对象
    BeanUtils.copyProperties(dishDto,dish);
    //mybatis在新增数据成功时会自动返回数据库生成的主键id
    //springboot+mybatisplus框架之所以要通过注解返回主键id是因为，那个例子重主键id的生成方式时雪花算法
    //并不是通过数据库生成的，若是不添加insert操作会自动返回的还是雪花id，不过添加注解会显得的你很专业
    dishMapper.insert(dish);
    List<DishFlavor> flavors = dishDto.getFlavors();
    for (DishFlavor flavor : flavors) {
      flavor.setDishId(dish.getId());
      dishFlavorMapper.insert(flavor);
    }

    redisTemplate.delete(RedisContant.DISH);
    return Result.success("添加成功");

  }

  @Override
  public Result getDishByPage(Integer page, Integer pageSize, String name) {
    IPage<Dish> setPage = new Page<>(page,pageSize);

    IPage<Dish> dishIPage;
    if(null != name){
      LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
      wrapper.like(Dish::getName,name);
      dishIPage = dishMapper.selectPage(setPage, wrapper);
    } else {
      dishIPage = dishMapper.selectPage(setPage,null);
    }

    List<Dish> dishes = dishIPage.getRecords();
    ArrayList<DishDto> dishDtos;
   /* for (Dish dish : dishes) {
      Long categoryId = dish.getCategoryId();
      Category category = categoryMapper.selectById(categoryId);
      DishDto dishDto = new DishDto();
      BeanUtils.copyProperties(dish,dishDto);
      dishDto.setCategoryName(category.getName());
      dishDtos.add(dishDto);
    }*/
    String jsonDishes = JSON.toJSONString(dishes);
    dishDtos = (ArrayList<DishDto>) JSON.parseArray(jsonDishes, DishDto.class);
    dishDtos.forEach(c->{                            //foreach本质上是个迭代器，并不能改变原有的数据属性，一般而言只能做遍历使用，但对于引用型数据，因为其本质是个指针，可以做到对于地址内容的修改，故可以修改原有数据哈
      Category category = categoryMapper.selectById(c.getCategoryId());
      c.setCategoryName(category.getName());
    });
    IPage<DishDto> dishDtoPage = new Page<>();
    BeanUtils.copyProperties(dishIPage,dishDtoPage);
    dishDtoPage.setRecords(dishDtos);
/*    dishDtoPage.setRecords(dishDtos);
    dishDtoPage.setTotal(dishIPage.getTotal());
    dishDtoPage.setCurrent(dishIPage.getCurrent());
    dishDtoPage.setSize(dishIPage.getSize());
    dishDtoPage.setPages(dishIPage.getPages());*/
    return Result.success(dishDtoPage);
  }

  @Override
  public Result getDishById(String id) {
    if (null == id){
      return Result.error("参数错误");
    }
    Dish dish = dishMapper.selectById(id);
    LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(DishFlavor::getDishId,id);
    List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(wrapper);
    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish,dishDto);
    dishDto.setFlavors(dishFlavors);
    return Result.success(dishDto);
  }

  @Override
  public Result update(DishDto dishDto) {
    if (null == dishDto){
      return Result.error("参数错误");
    }
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDto,dish);
    dishMapper.updateById(dish);
    LambdaUpdateWrapper<DishFlavor> dishFlavorLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    dishFlavorLambdaUpdateWrapper.eq(DishFlavor::getDishId,dishDto.getId());
    dishFlavorMapper.delete(dishFlavorLambdaUpdateWrapper);
    for (DishFlavor flavor : dishDto.getFlavors()) {
      flavor.setDishId(dishDto.getId());
      dishFlavorMapper.insert(flavor);
    }
    redisTemplate.delete(RedisContant.DISH);
    return Result.success("修改成功");
  }

  @Override
  public Result deleteByIds(Long[] ids) {
    //测试点
    log.info("ids:"+ Arrays.toString(ids));
    dishMapper.deleteBatchIds(Arrays.asList(ids));
    for (Long id : ids) {
      dishFlavorMapper.deleteById(id);
    }
    redisTemplate.delete(RedisContant.DISH);
    return Result.success("删除成功");
  }

  @Override
  public Result motify(Integer status, Long[] ids) {
  /*  for (Long id : ids) {
      LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
      wrapper.eq(Dish::getId,id);
      //Dish dish = dishMapper.selectById(id);
      Dish dish = new Dish();
      wrapper.set(Dish::getStatus,status);
      dishMapper.update(dish,wrapper);
    }*/

    //Dish dish = new Dish();
    LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
    wrapper.set(Dish::getStatus,status).in(Dish::getId,ids);
    dishMapper.update(null,wrapper);
    redisTemplate.delete(RedisContant.DISH);
    return Result.success("修改成功");
  }

  @Override
  public Result getListByCategoryId(Long categoryId) {

    List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForHash().get(RedisContant.DISH, RedisContant.CATEGORYDISH+categoryId);

    if (dishDtos ==null||dishDtos.size() == 0) {
      System.out.println("我是从数据库中查询的数据");
      LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
      dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);
      List<Dish> dishes = dishMapper.selectList(dishLambdaQueryWrapper);

      String jsonString = JSON.toJSONString(dishes);
      dishDtos = JSON.parseArray(jsonString, DishDto.class);
      for (DishDto dishDto : dishDtos) {
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(
            new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dishDto.getId()));
        dishDto.setFlavors(dishFlavors);
      }
      redisTemplate.opsForHash().put(RedisContant.DISH,RedisContant.CATEGORYDISH+categoryId,dishDtos);
    } else {
      System.out.println("我是从缓存中查询的数据！");
    }
    return Result.success(dishDtos);
  }


}
