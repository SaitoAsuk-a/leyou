package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: taft
 * @Date: 2018-8-16 16:08
 */
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public ResponseEntity<List<Category>> findCategoryByPid(
       @RequestParam(value = "pid",defaultValue = "0") Long pid){

        List<Category> categoryList = categoryService.findCategoryByPid(pid);

        //没有查到返回404
        if (categoryList==null||categoryList.size()==0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("names")
    public ResponseEntity<List<String>> queryCategoryNameById(@RequestParam("ids") List<Long> ids){
        List<String> nameList = this.categoryService.queryCategoryNameByCids(ids);

        if (nameList==null||nameList.size()==0){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ResponseEntity.ok(nameList);

    }

}
