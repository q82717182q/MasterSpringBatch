package com.yao.masterspringbatch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Created by Jack Yao on 2021/12/3 7:47 上午
 */@Slf4j
@Component
public class HWJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info("before start a job" + jobExecution);
        log.info("jobExecution.getStatus()" + jobExecution.getStatus());
        log.info("jobExecution.getJobInstance().getJobName()" + jobExecution.getJobInstance().getJobName());
        log.info("before jobExecution.getJobConfigurationName()" + jobExecution.getJobConfigurationName());
        jobExecution.getExecutionContext().put("my name","Jack");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("after start a job");
        log.info(" - OOOOO - jobExecution.getExecutionContext() - OOOOO - : "+jobExecution.getExecutionContext());

    }
}
