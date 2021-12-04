package com.yao.masterspringbatch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Created by Jack Yao on 2021/12/4 9:54 上午
 */

@Slf4j
@Component
public class HWStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info(" - OOOOO - before stepExecution - OOOOO - : "+stepExecution);
        log.info(" - OOOOO - before stepExecution.getJobExecution().getExecutionContext()- OOOOO - : "+stepExecution.getJobExecution().getExecutionContext());
        stepExecution.getExecutionContext().put("her name","catherine");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(" - OOOOO - after stepExecution - OOOOO - : "+stepExecution);
        log.info(" - OOOOO - stepExecution.getExecutionContext() - OOOOO - : "+stepExecution.getExecutionContext());
        log.info(" - OOOOO - stepExecution.getExecutionContext() - OOOOO - : "+stepExecution.getExecutionContext());
        log.info(" - XXXXX - stepExecution.getJobExecution().getJobParameters() - OOOOO - : "+stepExecution.getJobExecution().getJobParameters());


        return null;
    }
}
