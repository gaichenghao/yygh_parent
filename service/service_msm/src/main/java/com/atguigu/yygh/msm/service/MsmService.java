package com.atguigu.yygh.msm.service;

public interface MsmService {
    //发送手机短信
    boolean send(String phone, String code);

    //发送手机短信
    boolean send(String phone, String code,boolean isNew);
}
