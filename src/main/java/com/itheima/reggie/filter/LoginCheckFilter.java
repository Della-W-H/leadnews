package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.config.BaseContext;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter extends HttpFilter {

  //springboot提供的路径字符串匹配工具
  private static final AntPathMatcher matcher = new AntPathMatcher();

  @Override
  protected void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String requestURI = request.getRequestURI();
    log.info("拦截到的请求：{}",requestURI);

    String[] urls = {
        "/employee/login",
        "/employee/logout",
        "/backend/**",
        "/front/**",
        "/common/**",
        "/user/**",
        "/wx"
    };
    for (String url : urls) {
      boolean match = matcher.match(url, requestURI);
      if(match){
        System.out.println("此请求被放行了...");
        chain.doFilter(request,response);
        return;
      }
    }
    System.out.println("此请求被拦截了.....");

    //获得session判断登录信息
    HttpSession session = request.getSession();
    Long id = (Long) session.getAttribute("employee");

    if(id == null){
      String jsonString = JSON.toJSONString(Result.error("NOTLOGIN"));
      //测试点
      System.out.println("我是拦截返回前端的json数据："+jsonString);
      response.getOutputStream().write(jsonString.getBytes());
      return;
    }
    //最终放行,即用户已经登陆了
    BaseContext.threadLocal.set(id);
    chain.doFilter(request,response);
  }
}
