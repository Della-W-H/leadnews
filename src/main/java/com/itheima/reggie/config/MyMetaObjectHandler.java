package com.itheima.reggie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    log.info("我是insert通用代码添加块");
    Long id = BaseContext.threadLocal.get();
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("updateTime",LocalDateTime.now());
    metaObject.setValue("createUser",id);
    metaObject.setValue("updateUser",id);
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    log.info("我是update通用代码添加块");
    Long id = BaseContext.threadLocal.get();
    metaObject.setValue("updateTime",LocalDateTime.now());
    metaObject.setValue("updateUser",id);
  }
}
