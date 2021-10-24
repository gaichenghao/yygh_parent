package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospApiController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){

        Page<Hospital> page1 = hospitalService.selectHospPage(page, limit, hospitalQueryVo);

        return Result.ok(page1);

    }

    @ApiOperation(value = "根据医院名称查询")
    @GetMapping("findByHosName/{hosName}")
    public Result findByHosName(@PathVariable String hosName){
        List<Hospital> list =hospitalService.findByHosname(hosName);
        return Result.ok(list);
    }

}
