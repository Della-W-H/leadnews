package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

  @Update("UPDATE employee SET status=#{status} WHERE id=#{id}")
  void updateByIdG(Employee employee);

  String name = "你真的好叼啊！";
}
