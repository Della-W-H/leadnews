package com.itheima.reggie.service;

import com.itheima.reggie.entity.User;

public interface UserService {

  User findByPhone(String phone);

  User add(String phone);
}
