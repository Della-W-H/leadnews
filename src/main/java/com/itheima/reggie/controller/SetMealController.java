package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.SetmealDto;
import com.itheima.reggie.service.SetmealService;
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
@RequestMapping("/setmeal")
public class SetMealController {

  private final SetmealService setmealService;

  @Autowired
  public SetMealController(SetmealService setmealService) {
    this.setmealService = setmealService;
  }

  @PostMapping
  private Result add(@RequestBody SetmealDto setmealDto){
    return setmealService.add(setmealDto);
  }

  @GetMapping("/page")
  public Result page(Integer page, Integer pageSize, String name){
    return setmealService.page(page,pageSize,name);
  }

  @DeleteMapping
  public Result deleteByIds(Long[] ids){
    return setmealService.deleteByIds(ids);
  }

  @GetMapping("/{id}")
  public Result getById(@PathVariable Long id){
    return setmealService.getById(id);
  }

  @PutMapping
  public Result update(@RequestBody SetmealDto setmealDto){
    return setmealService.update(setmealDto);
  }

  @PostMapping("/status/{changeStatus}")
  public Result motify(Long[] ids,@PathVariable Integer changeStatus){
    return setmealService.changeStatus(ids,changeStatus);
  }

  @GetMapping("/list")
  public Result findAll(Long categoryId,Integer status){
    return setmealService.findAll(categoryId,status);
  }
}
