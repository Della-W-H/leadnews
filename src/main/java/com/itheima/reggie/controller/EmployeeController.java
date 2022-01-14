package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private HttpServletRequest request;

  @PostMapping("/login")
  public Result login(@RequestBody Employee employee){

    //int a = 1/0;

    Result result = employeeService.login(employee);
    if(result.getCode() == 1){
      //代表登录成功,存储登录对象的session
      HttpSession session = request.getSession();
      session.setAttribute("employee",((Employee)result.getData()).getId());
    }
    return result;
  }

  @PostMapping("/logout")
  public Result logout(){
    HttpSession session = request.getSession();
    session.invalidate();
    return Result.success("退出成功");
  }

  @PostMapping
  public Result add(@RequestBody Employee employee){
    //判断参数
    if(employee == null){
      return Result.error("参数错误");
    }
    //进行登录判断
    HttpSession session = request.getSession();
    Long userId = (Long) session.getAttribute("employee");
    if(userId == null){
      return Result.error("请先登录");
    }
    //补全参数
    //1.补全密码
    String password = DigestUtils.md5DigestAsHex("123456".getBytes());
    employee.setPassword(password);

    employeeService.save(employee);
    return Result.success("添加成功");
  }

  @GetMapping("/page")
  public Result page(Integer page,Integer pageSize, String name){
    //测试点
    System.out.println("我是分页查询前端参数1："+page+"  "+pageSize+"  "+name);

    return employeeService.findByPageSoon(page,pageSize,name);
  }


  /*
  *   员工状态的改变
  * */
  @PutMapping()
  public Result updateStatus(@RequestBody Employee employee){
    //测试点
    System.out.println("我是前端传过来的要修改的员工参数："+employee);

    return employeeService.updateStatus(employee);
  }

  /*
  *  根据id查询数据，用于修改的回显操作
  *  使用restful风格进行传参
  * */
  @GetMapping("/{id}")
  public Result findById(@PathVariable Long id){
    Employee employee = employeeService.findById(id);
    return Result.success(employee);
  }
}





























