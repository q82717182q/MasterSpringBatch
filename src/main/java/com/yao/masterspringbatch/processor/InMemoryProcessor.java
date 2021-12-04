package com.yao.masterspringbatch.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Jack Yao on 2021/12/4 11:18 上午
 */
@Slf4j
@Component
public class InMemoryProcessor implements ItemProcessor<Integer,Integer> {
    @Override
    public Integer process(Integer item) throws Exception {
        log.info(" - XXXXX - process - OOOOO - : ");

        return Integer.sum(10,item);
    }
}
