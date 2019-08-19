package com.leyou.page.controller;

import com.leyou.page.service.FileService;
import com.leyou.page.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private FileService fileService;
    /**
     * 跳转到商品详情页
     *
     * 第一次查看详情走的是数据库
     *
     * 从第二次以及第二次之后，就不应该走数据库
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){


        model.addAllAttributes(goodsService.loadData(id));

        // 判断是否需要生成新的页面
        if(!this.fileService.exists(id)){
            this.fileService.syncCreateHtml(id);
        }
        return "item";
    }
}