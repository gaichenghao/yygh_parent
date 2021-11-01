package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import com.atguigu.yygh.oss.service.FileService;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceImpl implements FileService {

    //上传文件到阿里云
    @Override
    public String upload(MultipartFile file) {


        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建CreateBucketRequest对象。
        CreateBucketRequest createBucketRequest = new CreateBucketRequest("yygh-gaichtest");

        // 如果创建存储空间的同时需要指定存储类型和数据容灾类型, 请参考如下代码。
        // 此处以设置存储空间的存储类型为标准存储为例介绍。
        //createBucketRequest.setStorageClass(StorageClass.Standard);
        // 数据容灾类型默认为本地冗余存储，即DataRedundancyType.LRS。如果需要设置数据容灾类型为同城冗余存储，请设置为DataRedundancyType.ZRS。
        //createBucketRequest.setDataRedundancyType(DataRedundancyType.ZRS);
        // 设置存储空间的权限为公共读，默认为私有。
        //createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);

        // 创建存储空间。
        ossClient.createBucket(createBucketRequest);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
