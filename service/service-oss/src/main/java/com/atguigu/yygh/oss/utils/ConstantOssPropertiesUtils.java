package com.atguigu.yygh.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantOssPropertiesUtils implements InitializingBean {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    public static String Endpoint_Id;
    public static String ACCESS_KEY_ID;
    public static String AccessKey_Secret;

    @Override
    public void afterPropertiesSet() throws Exception {
        Endpoint_Id=endpoint;
        ACCESS_KEY_ID=accessKeyId;
        AccessKey_Secret=accessKeySecret;
    }
}
