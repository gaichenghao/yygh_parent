package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.atguigu.easyexcel.UserData;

import java.util.ArrayList;
import java.util.List;

public class TestWrite {
    public static void main(String[] args) {
        UserData a=new UserData();
        a.setUid(1);
        a.setUsername("gch");
        List<UserData> userDataLis=new ArrayList<>();
        userDataLis.add(a);

        //设置excel文件路径和文件名称
        String fileName="F:\\excel\\01.xlsx";

        //调用方法实现写操作
        EasyExcel.write(fileName,UserData.class).sheet("用户信息").doWrite(userDataLis);

    }
}
