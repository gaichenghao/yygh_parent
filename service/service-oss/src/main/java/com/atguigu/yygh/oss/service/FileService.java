package com.atguigu.yygh.oss.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
    //上传文件到阿里云
    String upload(MultipartFile file);
}
