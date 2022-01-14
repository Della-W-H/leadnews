package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @PostMapping()
  public Result addCategory(@RequestBody Category category){
    log.info("我是添加套餐的前端参数："+category);
    return categoryService.add(category);
  }

  @GetMapping("/page")
  public Result findByPage(Integer page,Integer pageSize){
    return categoryService.findByPage(page,pageSize);
  }

  @DeleteMapping()
  public Result deleteById(Long id){
    return categoryService.deleteById(id);
  }

  @PutMapping
  public Result update(@RequestBody Category category){
    return categoryService.update(category);
  }

  @GetMapping("/list")
  public Result getByType(Integer type){
    return categoryService.getByType(type);
  }
}
