package com.itheima.reggie.entity.Dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import java.util.List;
import lombok.Data;

@Data
public class DishDto extends Dish {
   private List<DishFlavor> flavors;

   private String categoryName;
   private Integer copies;
}
