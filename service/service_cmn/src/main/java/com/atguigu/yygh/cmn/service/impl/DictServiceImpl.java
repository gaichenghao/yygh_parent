package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl
        extends ServiceImpl<DictMapper, Dict>
        implements DictService {

//    @Autowired
//    private DictMapper dictMapper;

    //根据数据id查询子数据列表
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //像list集合中每个dict对象中的设置hasChildren
        for (Dict dict:dictList) {
            dict.setHasChildren(this.isChildren(dict.getId()));
        }
        return dictList;
    }

    //导出数据字典接口
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName="dict";
        response.setHeader("Conteny-disposition","attachment;filename="+fileName+".xlsx");
        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict--DictEevo
        List<DictEeVo> dictEeVos=new ArrayList<>();
        for(Dict dict:dictList){
            DictEeVo dictEeVo=new DictEeVo();
            //dictEevo.setId(dict.getId())
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVos.add(dictEeVo);
        }
        try {
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict").doWrite(dictEeVos);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //导入数据字典
    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据dictcode和value查询
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空 直接根据value查询
        if(StringUtils.isEmpty(dictCode)){
            //直接根据value查询
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else{//如果dictCode不为空，根据dictCode和value查询
            //根据dictcode查询dict对象，得到dict的id值

            Dict dictByDictCode = this.getDictByDictCode(dictCode);
            Long parent_id = dictByDictCode.getId();
            //根据dictId和value查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getName();

        }
    }

    //根据dictCode获取下级截点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictCode获取对应id
        Dict dictByDictCode = this.getDictByDictCode(dictCode);
        //获取子节点
        List<Dict> chlidData = this.findChlidData(dictByDictCode.getId());
        return chlidData;
    }

    public Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("dict_Code",dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return  dict;
    }


    //判断id下面是否有子节点
    public boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
//        if(count>0){
//            return true;
//        }else {
//            return false;
//        }
        return count>0?true:false;
    }


}
