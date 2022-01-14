package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.DaS;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

  @Autowired
  private ShoppingCartService shoppingCartService;
  @Autowired
  private HttpServletRequest request;

  @GetMapping("/list")
  public Result getList(){
    HttpSession session = request.getSession();
    Long id = (Long) session.getAttribute("employee");
    return shoppingCartService.getList(id);
  }

  @PostMapping("/add")
  public Result add(@RequestBody ShoppingCart shoppingCart){
    HttpSession session = request.getSession();
    Long id = (Long) session.getAttribute("employee");
    shoppingCart.setUserId(id);
    return shoppingCartService.add(shoppingCart);
  }

  @DeleteMapping("/clean")
  public Result deleteById(){
    HttpSession session = request.getSession();
    Long id = (Long) session.getAttribute("employee");
    return shoppingCartService.deleteById(id);
  }

  @PostMapping("/sub")
  public Result sub(@RequestBody DaS daS){
    HttpSession session = request.getSession();
    Long id = (Long) session.getAttribute("employee");
    return shoppingCartService.subById(daS,id);
  }
}
