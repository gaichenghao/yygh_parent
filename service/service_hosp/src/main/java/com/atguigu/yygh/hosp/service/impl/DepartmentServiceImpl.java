package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;


    //上传科室的接口
    @Override
    public void save(Map<String, Object> patamMap) {
        //patamMap转化为department对象
        String patamMapString = JSONObject.toJSONString(patamMap);
        Department department = JSONObject.parseObject(patamMapString, Department.class);

        //根据医院编号和科室编号查询
        Department departmentExist =departmentRepository.
                getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        //判断
        if(departmentExist!=null){
            departmentExist.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }

    //查询科室接口
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {

        //创建pageable对象 设置当前页和每页记录数
        Pageable pageable= PageRequest.of(page-1,limit);

        //创建example对象
        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example=Example.of(department,matcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);

        return all;
    }

    //删除科室接口
    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询


        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            departmentRepository.deleteById(department.getId());
        }

    }

    //根据医院编号 查询所有科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合 用户最终数据封装
        List<DepartmentVo> result=new ArrayList<>();

        //根据医院编号 查询医院所有科室信息
        Department departmentQuery=new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室信息
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室分组 bigcode 编号 获取每个大科室里面下级子科室

        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        //遍历map集合
        for(Map.Entry<String,List<Department>> entry:departmentMap.entrySet()){
            //大科室编号
            String bigCode=entry.getKey();
            //得到大科室编号下的全部数据
            List<Department> departments = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo=new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departments.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children=new ArrayList<>();
            for(Department department1:departments){
                DepartmentVo departmentVo2=new DepartmentVo();
                departmentVo2.setDepcode(department1.getDepcode());
                departmentVo2.setDepname(department1.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室List集合放到大科室children里面
            departmentVo.setChildren(children);
            result.add(departmentVo);
        }
        return result;
    }

    //查询科室名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department!=null){
            return department.getDepname();
        }
        return null;
    }
}
