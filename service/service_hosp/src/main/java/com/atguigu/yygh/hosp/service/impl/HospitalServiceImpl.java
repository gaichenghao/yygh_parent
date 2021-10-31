package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> patamMap) {
        //把参数map集合转换对象 Hospital

        String mapperString = JSONObject.toJSONString(patamMap);
        Hospital hospital = JSONObject.parseObject(mapperString, Hospital.class);



        //先判断是否存在相同的数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist=hospitalRepository.getHospitalByHoscode(hoscode);



        //如果过有 则更新
        if(hospitalExist!=null){
            hospitalExist.setStatus(hospitalExist.getStatus());
            hospitalExist.setCreateTime(hospitalExist.getCreateTime());
            hospitalExist.setUpdateTime(new Date());
            hospitalExist.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
        else{        //如果过不存在 进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);

        }





    }

    //根据医院编号查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    //医院列表（条件查询分页）
    @Override
    public Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建pageable对象
        Pageable pageabl= PageRequest.of(page-1,limit);
        //创建条件匹配器
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalQueryVo转换成hospital对象
        Hospital hospital=new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example=Example.of(hospital,matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageabl);

        //List<Hospital> content = pages.getContent();

        //获取查询list集合,遍历医院等级封装

        pages.getContent().stream().forEach(item-> {
            this.setHopitalHosType(item);
        });

        return pages;
    }



    //获取查询list集合,遍历医院等级封装
    private Hospital setHopitalHosType(Hospital hospital) {
        //根据diccode和value获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        hospital.getParam().put("hostypeString",hostypeString);
        //查询省市地区
        String provinceString = dictFeignClient.getName( hospital.getProvinceCode());
        String cityString = dictFeignClient.getName( hospital.getCityCode());
        String districtString = dictFeignClient.getName( hospital.getDistrictCode());
        hospital.getParam().put("fullAddress",provinceString+cityString+districtString);
        return hospital;

    }

    //更新医院上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id获取医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);


    }


    //医院详情信息
    @Override
    public Map<String,Object> getHospById(String id) {
        Map<String,Object> map=new HashMap<>();

        //根据id获取医院信息
        Hospital hospital = this.setHopitalHosType(hospitalRepository.findById(id).get());
        //医院的基本信息（包含医院等级）
        map.put("hospital",hospital);
        //单独处理更直观
        map.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return map;
    }

    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital!=null){
            return hospital.getHosname();
        }
        return null;
    }

    //根据医院名称查询
    @Override
    public List<Hospital> findByHosname(String hosName) {
        return hospitalRepository.findHospitalByHosnameLike(hosName);
    }

    //根據醫院編號獲取医院预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> result=new HashMap<>();
        //医院详情
        Hospital hospital = this.setHopitalHosType(this.getByHoscode(hoscode));
        //预约规则
        result.put("hospital",hospital);
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        return result;
    }
}
