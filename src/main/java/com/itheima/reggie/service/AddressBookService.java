package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

  Result deleteBatch(Long[] ids);
}