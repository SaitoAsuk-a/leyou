package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: taft
 * @Date: 2018-8-16 16:15
 */
public interface CategoryMapper extends Mapper<Category>,SelectByIdListMapper<Category,Long> {
}
