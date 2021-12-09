package com.yao.masterspringbatch.config;

import com.yao.masterspringbatch.listener.HWJobExecutionListener;
import com.yao.masterspringbatch.listener.HWStepExecutionListener;
import com.yao.masterspringbatch.listener.ProductSkipListener;
import com.yao.masterspringbatch.model.Product;
import com.yao.masterspringbatch.processor.InMemoryProcessor;
import com.yao.masterspringbatch.processor.ProductProcessor;
import com.yao.masterspringbatch.reader.InMemoryReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
    @Autowired
    private DataSource dataSource;

    @Bean
    public Job helloWorldJob() {
        return jobs.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
//                .listener(hwJobExecutionListener)
                .start(step1())
//                .next(step2())
//                .next(multiThreadStep())/* 改成用multiThread 測試用多少時間 */
                .next(asyncStep())
                .build();

    }

    @Bean
    public Step step1() {
        return steps.get("step1")
                .listener(hwStepExecutionListener)
                .tasklet(helloWorld())
                .build();
    }

    //這邊chunkSize = 3就是說reader抓到一次 processor處理一次，這樣動作三次之後，東西往後執行給writer寫出
    //這邊展示三種呼叫方式，直接創一個方法、注入、直接new
    @Bean
    public Step step2() {
        return steps.get("step2")
                .<Integer, Integer>chunk(5)
                .reader(flatFileItemReader(null))
//                .reader(xmlItemReader(null))
//                .reader(jdbcCursorItemReader())
                .processor(new ProductProcessor())
//                .writer(dbWriter2())
                .writer(flatFileItemWriter(null))
                .faultTolerant()
//                .skip(FlatFileParseException.class)
//                .skip(RuntimeException.class)/*測試ProductSkipListener.onSkipProcess用*/
                .skipLimit(10)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())/*這個三個階段錯誤都會跳過itemRPW*/
                .listener(new ProductSkipListener())
                .build();
    }

    /* multiThread */
    @Bean
    public Step multiThreadStep() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();

        return steps.get("multiThreadStep")
                .<Integer, Integer>chunk(5)
                .reader(flatFileItemReader(null))
//                .reader(xmlItemReader(null))
//                .reader(jdbcCursorItemReader())
                .processor(new ProductProcessor())
                .writer(dbWriter2())
//                .writer(flatFileItemWriter(null))
                .taskExecutor(taskExecutor)
//                .faultTolerant()
//                .skip(FlatFileParseException.class)
//                .skip(RuntimeException.class)/*測試ProductSkipListener.onSkipProcess用*/
//                .skipLimit(10)
//                .skipPolicy(new AlwaysSkipItemSkipPolicy())/*這個三個階段錯誤都會跳過itemRPW*/
//                .listener(new ProductSkipListener())
                .build();
    }


    @Bean
    public AsyncItemProcessor asyncItemProcessor(){
        AsyncItemProcessor processor = new AsyncItemProcessor();
        return null;
    }
    /* async 用不到taskExecutor*/
    @Bean
    public Step asyncStep() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();

        return steps.get("asyncStep")
                .<Integer, Integer>chunk(5)
                .reader(flatFileItemReader(null))
//                .reader(xmlItemReader(null))
//                .reader(jdbcCursorItemReader())
                .processor(new ProductProcessor())
//                .writer(dbWriter2())
                .writer(flatFileItemWriter(null))
//                .faultTolerant()
//                .skip(FlatFileParseException.class)
//                .skip(RuntimeException.class)/*測試ProductSkipListener.onSkipProcess用*/
//                .skipLimit(10)
//                .skipPolicy(new AlwaysSkipItemSkipPolicy())/*這個三個階段錯誤都會跳過itemRPW*/
//                .listener(new ProductSkipListener())
                .build();
    }



    @StepScope
    @Bean
    public FlatFileItemWriter flatFileItemWriter(
            @Value("#{jobParameters[fileOutput]}") FileSystemResource outputFile
    ) {
        FlatFileItemWriter writer = new FlatFileItemWriter<Product>() {
            @Override
            public String doWrite(List<? extends Product> items) {
//                for (Product p : items) {
//                    if (p.getProductId() == 9) {
//                        throw new RuntimeException("Because ID is 9");
//                    }
//                }
                return super.doWrite(items);
            }
        };
        writer.setResource(outputFile);
        writer.setLineAggregator(new DelimitedLineAggregator() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor() {
                    {
                        setNames(new String[]{"productId", "productName", "price", "unit", "productDesc"});
                    }
                });
            }
        });
        return writer;
    }

    public InMemoryReader reader() {
        return new InMemoryReader();
    }

    @StepScope
    @Bean
    public StaxEventItemReader xmlItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource inputFile) {

        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(inputFile);
        reader.setFragmentRootElementName("product");/*要設定給reader 知道哪個tag是root element的名稱*/
        /*tell reader how to parse XML and which domain object to be mapped*/
        reader.setUnmarshaller(new Jaxb2Marshaller() {/*這邊還要添加一些依賴spring oxm O/XMapper 就是object 與 xml轉換用的*/
            {
                setClassesToBeBound(Product.class);
            }
        });
        return reader;
    }

    /*@Value set the resource*/
    @StepScope
    @Bean
    public FlatFileItemReader flatFileItemReader(
            @Value("#{jobParameters['inputFile']}")
                    FileSystemResource inputFile) {
        /*step 1 let reader know where is the file*/
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setResource(inputFile); /*目錄要寫對最前面不用斜線*/
        /*step 2 create the line Mapper which map the line to the product8*/
        reader.setLineMapper(
                new DefaultLineMapper<Product>() {
                    {
                        /*how to break each line by break multiple tokens*/
                        setLineTokenizer(new DelimitedLineTokenizer() {
                            {
                                /*you want to give a name to each token，there names are mapping to the product*/
                                setNames(new String[]{"productID", "productName", "productDesc", "price", "unit"});
//                               /*if no set, delimiter default ","*/
                                setDelimiter(",");
                            }
                        });
                        /*with each token we need it to map to my object
                         * so we need to set target type*/
                        setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {
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

    //    @StepScope    /*這邊用不用這個好像沒差*/
    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader() {
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setSql("select product_id, product_name , product_desc as product_desc, unit, price from products");
        reader.setRowMapper(new BeanPropertyRowMapper() {
            {
                setMappedClass(Product.class);
            }
        });
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

    @Bean
    public JdbcBatchItemWriter dbWriter() {
        JdbcBatchItemWriter writer = new JdbcBatchItemWriter();
        writer.setDataSource(this.dataSource);
        writer.setSql("insert into products (product_id,product_name,product_desc,price,unit) " +
                "values (?,?,?,?,? )");
        writer.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<Product>() {
            @Override
            public void setValues(Product item, PreparedStatement ps) throws SQLException {
                ps.setInt(1, item.getProductId());
                ps.setString(2, item.getProductName());
                ps.setString(3, item.getProductDesc());
                ps.setBigDecimal(4, item.getPrice());
                ps.setInt(5, item.getUnit());
            }
        });
        return writer;
    }

    @Bean
    public JdbcBatchItemWriter dbWriter2() {

        return new JdbcBatchItemWriterBuilder<Product>()
                .dataSource(this.dataSource)
                .sql("insert into products (product_id,product_name,product_desc,price,unit) " +
                        "values (:productId, :productName, :productDesc, :price, :unit ) ")
                .beanMapped()
                .build();
    }
}
