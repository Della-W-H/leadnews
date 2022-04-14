package com;

import org.junit.Test;

/**
 * @author wanghong
 * @date 2022/4/14
 * @apiNote
 */
public class CasselDemo {

  @Test
  public void test(){
    byte a = 127;
    byte b = 127;
    a += b;
    System.out.println(a);
    //a = a + b;
    System.out.println(b);
  }
}
