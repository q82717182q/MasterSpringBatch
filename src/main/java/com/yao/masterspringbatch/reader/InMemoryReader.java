package com.yao.masterspringbatch.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack Yao on 2021/12/4 11:08 上午
 */
@Slf4j
public class InMemoryReader extends AbstractItemStreamItemReader {
    Integer[] numbers = {1,2,3,4,5,6,7,8,9,10};
    List<Integer> integerList = Arrays.asList(numbers);
    int index = 0;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Integer nextItem = null;
        if (index < integerList.size()){
            nextItem = integerList.get(index);
            index++;
        }else {
            index = 0;
        }
        log.info(" - XXXXX - read - OOOOO - : ");

        return nextItem;
    }
}
