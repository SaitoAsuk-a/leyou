package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: taft
 * @Date: 2018-9-1 18:30
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String, Object> loadData(Long spuId) {
        try {
            Map<String, Object> dataMap = new HashMap<>();

            //spu
            Spu spu = this.goodsClient.querySpuById(spuId);
            dataMap.put("spu", spu);
            //spuDetail
            SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spuId);
            dataMap.put("spuDetail", spuDetail);

            //skus
            List<Sku> skus = this.goodsClient.querySkuBySpuId(spuId);
            dataMap.put("skus", skus);

            //根据spu来查询具体的分类
            List<Category> categories = getCategories(spu);

            dataMap.put("categories",categories);

            //根据分类查询分类所具有的所有的组
            List<SpecGroup> groups = this.specificationClient.querySpecsByCid(spu.getCid3());
            dataMap.put("groups",groups);

            // 准备品牌数据
            List<Brand> brands = this.brandClient.queryBrandByIds(
                    Arrays.asList(spu.getBrandId()));
            dataMap.put("brand", brands.get(0));
            //最后需要加入规格参数
            //查询得到所有的规格参数,包含可以搜索以及不可以搜索的（所有包含通用以及特有，而且包含了是否可以索引）
            List<SpecParam> specParams = this.specificationClient.querySpecParam(null, spu.getCid3(), null, null);

            // 处理成id:name格式的键值对
            Map<Long,String> paramMap = new HashMap<>();
            for (SpecParam param : specParams) {
                paramMap.put(param.getId(), param.getName());//4 机身颜色，5，内存
            }
            dataMap.put("paramMap", paramMap);

            return dataMap;
        } catch (Exception e) {
            logger.error("封装数据异常{}", e);
            e.printStackTrace();
        }
        return null;
    }

    private List<Category> getCategories(Spu spu) {
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        List<Category> categories = new ArrayList<>();

        Category category1 = new Category();
        category1.setId(spu.getCid1());
        category1.setName(names.get(0));

        Category category2 = new Category();
        category2.setId(spu.getCid2());
        category2.setName(names.get(1));

        Category category3 = new Category();
        category3.setId(spu.getCid3());
        category3.setName(names.get(2));
        categories.add(category1);
        categories.add(category2);
        categories.add(category3);
        return categories;
    }
}
