package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBO;
import com.leyou.item.controller.GoodsController;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: taft
 * @Date: 2018-8-19 17:53
 */
@Service
public class GoodsService {

    //首先查询的一定是spu表
    //得到的结果，有两个值是不能直接使用（cid，bid）
    //需要把查询到的spu转为spobo，
    //根据spu中记录的category的信息去查分类的名称
    //根据spu中记录brand的信息去查询品牌名称

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private Logger logger = LoggerFactory.getLogger(GoodsService.class);

    public PageResult<SpuBO> queryGoods(Integer page, Integer rows, Boolean saleable, String key) {

        //开启分页

        PageHelper.startPage(page,rows);

        //构建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        if (null!=saleable){
            criteria.andEqualTo("saleable",saleable);
        }

        if (StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        Page<Spu> pageInfo = (Page<Spu>) spuMapper.selectByExample(example);

        //把这其中得spu变成spubo

        List<SpuBO> list = new ArrayList<>();

        //以下操作就是把spu转为SpuBO

        for (Spu spu : pageInfo.getResult()) {
            SpuBO spuBO = new SpuBO();

            //把spu中的所有值赋值给spuBO
            BeanUtils.copyProperties(spu,spuBO);

            //把spu中的品牌id取出，执行查询，把查到的品牌对象的名称赋值给spuBO
            spuBO.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());

            List<String> names = categoryService.queryCategoryNameByCids(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

            String join = StringUtils.join(names, "/");

            spuBO.setCname(join);

            list.add(spuBO);

        }

        return new PageResult<>(pageInfo.getTotal(),list);
    }


    @Transactional
    public void saveGoods(SpuBO spuBO) {

        // 保存spu
        spuBO.setSaleable(true);
        spuBO.setValid(true);
        spuBO.setCreateTime(new Date());
        spuBO.setLastUpdateTime(spuBO.getCreateTime());
        this.spuMapper.insert(spuBO);

        //保存spuDetail信息
        spuBO.getSpuDetail().setSpuId(spuBO.getId());

        spuDetailMapper.insert(spuBO.getSpuDetail());

        // 保存sku和库存信息
        saveSkuAndStock(spuBO.getSkus(), spuBO.getId());

        //发送消息
        sendMessage(spuBO.getId(),"insert");
    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) {

        for (Sku sku : skus) {
            if (!sku.getEnable()){
                continue;
            }
            //保存sku
            sku.setSpuId(spuId);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());

            this.skuMapper.insert(sku);

            //保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());

            this.stockMapper.insert(stock);

        }
    }

    public List<Sku> querySkuBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        return skuMapper.select(sku);
    }

    public SpuDetail querySpuDetailById(Long id) {

        return spuDetailMapper.selectByPrimaryKey(id);
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    /**
     *
     * @param id
     * @param type  操作的类型
     */
    private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            logger.error("{}商品消息发送异常，商品id：{}", type, id, e);
        }
    }

    public Sku querySkuById(Long id) {
        return this.skuMapper.selectByPrimaryKey(id);
    }
}
