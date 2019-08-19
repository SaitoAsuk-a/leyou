package com.leyou.index;

import com.leyou.LySearchService;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @Author: taft
 * @Date: 2018-8-29 16:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchService.class)
public class TestABC {
    @Autowired
    private CategoryClient categoryClient;

    private GoodsClient goodClient;

    @org.junit.Test
    public void test(){
        for (String s : categoryClient.queryNameByIds(Arrays.asList(1L, 2L, 3L))) {
            System.out.println("s = " + s);
        }


    }
}
