package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Dto.LoginMsgDto;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private HttpServletRequest request;

  @Autowired //redis链接工具类的注入
  private RedisTemplate redisTemplate;

  @PostMapping("/sendMsg")
  public Result sendMsg(@RequestBody LoginMsgDto loginMsgDto){
    Random random = new Random();
    int code = random.nextInt(899999)+100000;
    //SendSms.sendMsg(loginMsgDto.getPhone(),code+"");
    System.out.println("后端生成的验证码：" + code);

    //存储redis中字符串类型的key（手机号） value（验证码）
    redisTemplate.opsForValue().set(loginMsgDto.getPhone(),code+"");
    //设置过期时间
    redisTemplate.expire(loginMsgDto.getPhone(),300, TimeUnit.SECONDS);
  /*  HttpSession session = request.getSession();               //用redis取代session
    session.setAttribute(loginMsgDto.getPhone(),code+"");*/

    return Result.success("短信发送成功");
  }

  @PostMapping("/login")
  public Result login(@RequestBody LoginMsgDto loginMsgDto){
    if (loginMsgDto == null){
      return Result.error("参数错误");
    }
    HttpSession session = request.getSession();
    /*  String code = (String) session.getAttribute(loginMsgDto.getPhone());*/

    //通过redis获取验证码
    String code = (String) redisTemplate.opsForValue().get(loginMsgDto.getPhone());
    System.out.println("验证码的redis回取："+code);
    if(code == null){
      return Result.error("请先输入验证码");
    }
    if (!code.equals(loginMsgDto.getCode())){
      return Result.error("验证码错误");
    }
    User user = userService.findByPhone(loginMsgDto.getPhone());
    if (user == null){
      User newUser = userService.add(loginMsgDto.getPhone());
      session.setAttribute("employee",newUser.getId());
      redisTemplate.delete(loginMsgDto.getPhone());
    } else {
      session.setAttribute("employee",user.getId());
      redisTemplate.delete(loginMsgDto.getPhone());
    }
    return Result.success("登录成功");
  }

  @PostMapping("/loginout")
  public Result loginout(){
    HttpSession session = request.getSession();
    session.invalidate();
    return Result.success("退出成功");
  }
}
















