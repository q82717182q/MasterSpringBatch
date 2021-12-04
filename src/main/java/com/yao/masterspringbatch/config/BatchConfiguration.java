package com.yao.masterspringbatch.config;

import com.yao.masterspringbatch.listener.HWJobExecutionListener;
import com.yao.masterspringbatch.listener.HWStepExecutionListener;
import com.yao.masterspringbatch.model.Product;
import com.yao.masterspringbatch.processor.InMemoryProcessor;
import com.yao.masterspringbatch.reader.InMemoryReader;
import com.yao.masterspringbatch.writer.ConsoleWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
 * Created by Jack Yao on 2021/12/3 7:42 上午
 */
@Slf4j
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;
    @Autowired
    private StepBuilderFactory steps;
    @Autowired
    private HWJobExecutionListener hwJobExecutionListener;
    @Autowired
    private HWStepExecutionListener hwStepExecutionListener;
    @Autowired
    private InMemoryProcessor inMemoryProcessor;
    @Bean
    public Job helloWorldJob(){
        return jobs.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .listener(hwJobExecutionListener)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1(){
        return steps.get("step1")
                .listener(hwStepExecutionListener)
                .tasklet(helloWorld())
                .build();
    }
    //這邊chunkSize = 3就是說reader抓到一次 processor處理一次，這樣動作三次之後，東西往後執行給writer寫出
    //這邊展示三種呼叫方式，直接創一個方法、注入、直接new
    @Bean
    public Step step2(){
        return steps.get("step2")
                .<Integer,Integer>chunk(3)
                .reader(flatFileItemReader(null))
                .writer(new ConsoleWriter())
                .build();
    }



    public InMemoryReader reader(){
        return new InMemoryReader();
    }

    @StepScope
    @Bean
    public StaxEventItemReader xmlItemReader(@Value("#{jobParameters['inputFile']}")FileSystemResource inputFile){

        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(inputFile);
        return reader;
    }

    /*@Value set the resource*/
    @StepScope
    @Bean
    public FlatFileItemReader flatFileItemReader(
            @Value("#{jobParameters['inputFile']}")
            FileSystemResource inputFile){
        /*step 1 let reader know where is the file*/
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setResource(inputFile); /*目錄要寫對最前面不用斜線*/
        /*step 2 create the line Mapper which map the line to the product8*/
        reader.setLineMapper(
                new DefaultLineMapper<Product>(){
                    {
                        /*how to break each line by break multiple tokens*/
                        setLineTokenizer(new DelimitedLineTokenizer(){
                            {
                                /*you want to give a name to each token，there names are mapping to the product*/
                                setNames(new String[]{"productID","productName","ProductDesc","price","unit"});
//                               /*if no set, delimiter default ","*/
                                setDelimiter("|");
                            }
                        }) ;
                        /*with each token we need it to map to my object
                        * so we need to set target type*/
                        setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>(){
                            {
                                setTargetType(Product.class);
                            }
                        });
                    }

                }
        );
        /*step 3 tell reader to skip the header*/
        reader.setLinesToSkip(1);/*第一行是檔頭所以要跳過*/

        return reader;
    }

    private Tasklet helloWorld() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                log.info(" - OOOOO - Hello Spring Batch - OOOOO - :");
                return RepeatStatus.FINISHED;
            }
        };
    }
}