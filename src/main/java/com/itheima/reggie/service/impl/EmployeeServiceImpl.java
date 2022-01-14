package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired
  private EmployeeMapper mapper;

  @Override
  public Result login(Employee employee) {
    //校验参数是否存在
    if(employee == null){
      return Result.error("参数非法");
    }
    //将页面提交的密码进行password-MD5加密
    String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
    //根据页面提交的用户名进行查询数据库中员工的信息
    LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Employee::getUsername,employee.getUsername());
    //Employee employeeget = mapper.selectOne(wrapper);
    //继承的ServiceImpl提供了一些方法哦
    Employee employeeget = mapper.selectOne(wrapper);
    //如果没有查询到，则返回登陆失败
    if(employeeget==null){
      return Result.error("该员工不存在");
    }
    //密码比对，如果不一致，则放回登录失败
    if(!employeeget.getPassword().equals(password)){
      return Result.error("密码不正确");
    }
    //查看员工状态
    if(employeeget.getStatus()!=1){
      return Result.error("员工已禁用");
    }
    return Result.success(employeeget);
  }

  @Override
  public Result findByPageSoon(Integer page, Integer pageSize, String name) {

    Page<Employee> setPage = new Page<>(page, pageSize);
    Page<Employee> selectPage;
    if (name!=null) {
      LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
      wrapper.like(Employee::getName,name);
      selectPage = mapper.selectPage(setPage, wrapper);
    } else {
      selectPage = mapper.selectPage(setPage, null);
    }
    return Result.success(selectPage);
  }

  @Override
  public Result updateStatus(Employee employee) {
    //1。校验参数是否合法
    if(employee == null){
      return Result.error("参数非法");
    }

    //查询用户信息
    Employee employeeget = mapper.selectById(employee.getId());
    //如果存在修改员工状态
    if(null == employeeget){
      return Result.error("没有此员工");
    }
    employeeget.setStatus(employee.getStatus());
    mapper.updateById(employee);
    return Result.success("修改成功");
  }

  @Override
  public Employee findById(Long id) {
    return mapper.selectById(id);
  }

  @Override
  public void save(Employee employee) {
    mapper.insert(employee);
  }
}
