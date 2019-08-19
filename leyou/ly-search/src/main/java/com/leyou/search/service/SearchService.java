package com.leyou.search.service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: taft
 * @Date: 2018-8-29 18:29
 */
@Service
public class SearchService {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    private Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchResult search(SearchRequest searchRequest) {
        List<Category> categories = null;
        List<Brand> brands = null;

        Integer page = searchRequest.getPage() - 1;// page 从0开始
        Integer size = searchRequest.getSize();

        String key = searchRequest.getKey();

        if (key == null) {
            return null;
        }

        //查询构建工具
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //添加了查询的过滤，只要这些字段
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

        //获取基本的查询条件
        QueryBuilder basicQuery = buildBasicQueryWithFilter(searchRequest);

        //把查询条件添加到构建器中（这里仅仅是我的查询条件）
        queryBuilder.withQuery(basicQuery);

        //把分页条件条件到构建器中
        queryBuilder.withPageable(PageRequest.of(page,size));

        //获取排序的条件
        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            //把排序条件加给构建器
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }

        //对品牌以及分类做聚合
        String brandAggsName = "brands";

        String categoryAggsName = "categories";

        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggsName).field("brandId"));
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggsName).field("cid3"));

        AggregatedPage<Goods> goodsResult = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        long total = goodsResult.getTotalElements();
        long totalPages = (total + size - 1) / size;

        brands = getBrandAgg(brandAggsName,goodsResult);

        categories = getCategoryAgg(categoryAggsName,goodsResult);


        List<Map<String,Object>> specs = null;

        //只有搜索对应的分类个数是1的时候才能聚合规格参数
        //根据用户的搜索条件对应的产品分类查询产品对应的规格参数
        if (categories.size()==1){
            specs = getSpecs(categories.get(0).getId(),basicQuery);
        }

        return new SearchResult(total,totalPages,goodsResult.getContent(),categories,brands,specs);
    }

    //这个方法用来构建查询条件以及过滤条件
    private QueryBuilder buildBasicQueryWithFilter(SearchRequest searchRequest) {
        //构造布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        queryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()));

        //给这个查询加过滤
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        //取出map中的实体
        for (Map.Entry<String, String> entry : searchRequest.getFilter().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌不用前后加修饰
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }

        return queryBuilder.filter(filterQueryBuilder);
    }

    //规格参数的聚合应该和查询关联
    private List<Map<String, Object>> getSpecs(Long cid,QueryBuilder query) {

        List<Map<String,Object>> specs = new ArrayList<>();


        List<SpecParam> specParams = this.specificationClient.querySpecParam(null, cid, true, null);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //在做聚合之前先做查询，只有符合条件的规格参数才应该被查出来
        queryBuilder.withQuery(query);

        for (SpecParam specParam : specParams) {
            String name = specParam.getName();//内存，产地
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }

        AggregatedPage<Goods> aggs = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        Map<String, Aggregation> stringAggregationMap = aggs.getAggregations().asMap();

        for (SpecParam specParam : specParams) {
            Map<String,Object> spec = new HashMap<>();
            String name = specParam.getName();
            StringTerms stringTerms = (StringTerms) stringAggregationMap.get(name);
            List<String> val = new ArrayList<>();
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                val.add(bucket.getKeyAsString());
            }
            spec.put("k",name);//内存，存储空间，屏幕尺寸
            spec.put("options",val);

            specs.add(spec);
        }

        return specs;
    }

    private List<Category> getCategoryAgg(String categoryAggsName, AggregatedPage<Goods> goodsResult) {
        LongTerms longTerms = (LongTerms) goodsResult.getAggregation(categoryAggsName);
        List<Long> categoryIds = new ArrayList<>();
        for (LongTerms.Bucket bucket : longTerms.getBuckets()) {
            categoryIds.add(bucket.getKeyAsNumber().longValue());
        }
        List<String> names = this.categoryClient.queryNameByIds(categoryIds);

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Category category =new Category();
            category.setId(categoryIds.get(i));
            category.setName(names.get(i));
            categories.add(category);
        }
        return categories;
    }

    private List<Brand> getBrandAgg(String aggName,AggregatedPage<Goods> result){
        try {
            LongTerms longTerms1 = (LongTerms) result.getAggregation(aggName);

            List<Long> brandIds = new ArrayList<>();
            for (LongTerms.Bucket bucket : longTerms1.getBuckets()) {
                brandIds.add(bucket.getKeyAsNumber().longValue());
            }

            return this.brandClient.queryBrandByIds(brandIds);
        } catch (Exception e) {
            logger.error("解析品牌数据错误：{}",e);
            e.printStackTrace();
        }
        return null;
    }
}
