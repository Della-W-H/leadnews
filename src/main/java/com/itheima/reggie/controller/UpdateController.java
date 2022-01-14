package com.itheima.reggie.controller;

import com.itheima.reggie.commons.Result;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
@Slf4j
public class UpdateController {

  @Value("${fange.niubi}")
  private String basePath;

  @Autowired
  private HttpServletResponse response;

  @PostMapping("/upload")
  public Result upload(MultipartFile file) throws IOException {
    log.info("我是上传的文件："+file);

    String newFileName = UUID.randomUUID().toString();
    String oldName = file.getOriginalFilename();
    String hzName = oldName.substring(oldName.lastIndexOf("."));
    newFileName = newFileName + hzName;

    file.transferTo(new File(basePath+newFileName));
    return Result.success(newFileName);
  }

  @GetMapping("/download")
  public void download(String name) throws IOException {

    System.out.println(name);

    FileInputStream fis = new FileInputStream(basePath + name);
    ServletOutputStream os = response.getOutputStream();
    byte[] bytes = new byte[4096];
    while (true){
      int len = fis.read(bytes);
      if(len == -1){
        break;
      }
      os.write(bytes,0,len);
    }
    os.close();
    fis.close();
  }
}
