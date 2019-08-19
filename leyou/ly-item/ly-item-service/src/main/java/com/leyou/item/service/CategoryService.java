package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: taft
 * @Date: 2018-8-16 16:15
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> findCategoryByPid(Long pid) {
        Category category = new Category();
        //查询使用通用mapper，由于条件不是id主键所以要封装对象中
        category.setParentId(pid);

        return categoryMapper.select(category);
    }


    public List<String> queryCategoryNameByCids(List<Long> cids) {

        List<Category> categoryList = categoryMapper.selectByIdList(cids);

        List<String> names = new ArrayList<>();

        for (Category category : categoryList) {
            names.add(category.getName());
        }


        return names ;
    }
}
