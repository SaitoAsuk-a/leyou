package com.leyou;

import com.leyou.common.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liyu
 * @date 2019/8/21 15:54
 * @description 测试订单
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderTest {

    @Autowired
    private IdWorker idWorker;

    @Test
    public void getOrderId(){
        for (int i = 0; i < 50; i++) {
        long id = idWorker.nextId();
        System.out.println("id = " + id);
        }
    }

}
