package com.itheima.reggie.execption;

import com.itheima.reggie.commons.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
*  全局异常处理器 处理controller类所有的异常
* */
@Slf4j
@RestControllerAdvice
public class ExeccptionControllerAdvice {
  @ExceptionHandler(RuntimeException.class)
  public Result setExecptionResult(RuntimeException ex){
    log.error("出现了RuntimeExecption");
    ex.printStackTrace();
    return Result.error("服务器繁忙，请稍后再试.....");
  }
}