package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: taft
 * @Date: 2018-8-16 17:54
 */
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {

        //开启分页查询，page表示当前页，rows表示页容量
        PageHelper.startPage(page,rows);

        Example example = new Example(Brand.class);

        //包含排序字段
        if (StringUtils.isNotBlank(sortBy)){

            //根据排序的升序或者降序来设置排序的方式
            String orderClause = sortBy+ (desc? " DESC" : " ASC");

            example.setOrderByClause(orderClause);//id ASC
        }

        if(StringUtils.isNotBlank(key)){
            example.createCriteria().andLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }

        Page<Brand> brands = (Page<Brand>) brandMapper.selectByExample(example);

        PageResult<Brand> pageResult = new PageResult<>(brands.getTotal(),brands);

        return pageResult;
    }

    public void saveBrand(Brand brand, List<Long> cids) {
        //由通用mapper保存brand品牌
        brandMapper.insert(brand);

        for (Long cid : cids) {
            brandMapper.insertCategoryAndBrand(cid,brand.getId());
        }
    }

    public List<Brand> queryBrandsByCategoryId(Long cid) {
        return brandMapper.queryBrandsByCategoryId(cid);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
