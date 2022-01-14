package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.DishDto;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

  @Autowired
  private DishService dishService;

  @PostMapping
  public Result add(@RequestBody DishDto dishDto){
    return dishService.add(dishDto);
  }

  @GetMapping("/page")
  public Result getDishes(Integer page,Integer pageSize,String name){
    return dishService.getDishByPage(page,pageSize,name);
  }

  @GetMapping("/{id}")
  public Result getDish(@PathVariable String id){
    return dishService.getDishById(id);
  }

  @PutMapping
  public Result updateDish(@RequestBody DishDto dishDto){
    return dishService.update(dishDto);
  }

  @DeleteMapping
  public Result deleteIds(Long[] ids){
    return dishService.deleteByIds(ids);
  }

  @PostMapping("/status/{status}")
  public Result motifyStatus(@PathVariable Integer status, Long[] ids){
    return dishService.motify(status,ids);
  }

  @GetMapping("/list")
  public Result list(Long categoryId){
    return dishService.getListByCategoryId(categoryId);
  }
}
