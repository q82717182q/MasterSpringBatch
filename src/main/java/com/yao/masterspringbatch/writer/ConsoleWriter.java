package com.yao.masterspringbatch.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import java.util.List;

/**
 * Created by Jack Yao on 2021/12/4 11:21 上午
 */
@Slf4j
public class ConsoleWriter extends AbstractItemStreamItemWriter {

    @Override
    public void write(List items) throws Exception {
        log.info(" - XXXXX - write - OOOOO - : ");
//        items.stream().forEach(System.out::println);
        for (Object item: items) {
            log.info(" - XXXXX - item - OOOOO - : " + item);

        }
    }
}
