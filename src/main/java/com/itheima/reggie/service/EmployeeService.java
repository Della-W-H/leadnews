package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.Employee;

public interface EmployeeService {

  Result login(Employee employee);

  Result findByPageSoon(Integer page, Integer pageSize, String name);

  Result updateStatus(Employee employee);

  Employee findById(Long id);

  void save(Employee employee);
}
