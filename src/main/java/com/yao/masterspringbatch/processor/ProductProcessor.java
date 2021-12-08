package com.yao.masterspringbatch.processor;

import com.yao.masterspringbatch.model.Product;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by Jack Yao on 2021/12/5 7:00 PM
 */
@Component
public class ProductProcessor implements ItemProcessor<Product,Product> {
    @Override
    public Product process(Product item) throws Exception {
        Thread.sleep(300);/* 睡一下方便看出multi thread使用時間 */
        item.setProductDesc(item.getProductDesc().toUpperCase());
//        if (item.getProductId() == 2){
//            throw new RuntimeException("because ID is 2");
//        }else {
//            item.setProductDesc(item.getProductDesc().toUpperCase());
//        }
        return item;
    }
}
