package com.itheima.reggie.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.itheima.reggie.commons.Result;
import com.itheima.reggie.service.OrderService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx")
public class WxPayRecallController {

  @Autowired
  private OrderService orderService;

  @PostMapping("/notify")
  public void notifyWx(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    System.out.println("支付回调触发了.....");

    //将微信传过来的流转换未xml字符串
    String xml = convertToString(request.getInputStream());
    System.out.println(xml);
    //把xml转换成一个map集合
    Map<String, String> map = WXPayUtil.xmlToMap(xml);
    //支付的订单号 微信订单流水号
    String transaction_id = map.get("transaction_id");
    //代表返回是否初步确定支付成功
    String return_code = map.get("return_code");
    if (return_code.equals("SUCCESS")){ //进行初步成功与否的判断
      //根据订单号，查询微信端的支付状态
      String out_trade_no = map.get("out_trade_no");
      //调用微信的查询订单接口来查询此订单是否真的支付了
   /*   boolean flag = checkOrderStatus(out_trade_no);
      if (flag){ //即代表了真的支付了
        orderService.updateStatus(out_trade_no,transaction_id);

      }*/
    }
    response.setContentType("text/xml");
    String data = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    response.getWriter().write(data);
  }

  /**
   * 输入流转换为xml字符串
   * @param inputStream
   * @return
   */
  public static String convertToString(InputStream inputStream) throws
      IOException {
    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = 0;
    while ((len = inputStream.read(buffer)) != -1) {
      outSteam.write(buffer, 0, len);
    }
    outSteam.close();
    inputStream.close();
    String result = new String(outSteam.toByteArray(), "utf-8");
    return result;
  }
}
