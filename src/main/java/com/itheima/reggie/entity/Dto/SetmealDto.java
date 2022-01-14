package com.itheima.reggie.entity.Dto;

import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import java.util.List;
import lombok.Data;

@Data
public class SetmealDto extends Setmeal {
  private List<SetmealDish> setmealDishes;
  private String categoryName;//套餐分类名称
}
