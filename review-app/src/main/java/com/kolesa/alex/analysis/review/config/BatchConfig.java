package com.kolesa.alex.analysis.review.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.kolesa.alex.analysis.review.domain.Review;
import com.kolesa.alex.analysis.review.service.TranslateService;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "com.kolesa.alex.analysis.review.service")
public class BatchConfig {

    public static final String INPUT_FILE_PATH = "input/*.csv";
    public static final int CONCURRENT_LIMIT = 100;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean(transactionManager()).getJobRepository();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private TranslateService translateService;

    @Bean
    public ItemProcessor<Review,Review> reviewProcessor() {
        return item -> {
            Review result = new Review();
            result.setText(translateService.translate(item.getText()));
            result.setId(item.getId());
            return result;
        };
    }

    @Bean
    public ItemWriter<Review> reviewWriter() {
        return (item) -> {
            System.out.println(item);
        };
    }

    @Bean
    public ItemReader<Review> reviewReader()  {

        //configure line tokenizer
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer
                .setNames(new String[]{"id",
                        "ProductId",
                        "UserId",
                        "ProfileName",
                        "HelpfulnessNumerator",
                        "HelpfulnessDenominator",
                        "Score",
                        "Time",
                        "Summary",
                        "text"});

        //configure field set mapper
        FieldSetMapper<Review> fieldSetMapper = fieldSet -> {
            Review review = new Review();
            review.setId(fieldSet.readString(0));
            review.setText(fieldSet.readString(8));
            return review;
        };


        //configure line mapper
        DefaultLineMapper<Review> defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        //configure item reader
        FlatFileItemReader<Review> flatFileItemReader = new FlatFileItemReader();
        flatFileItemReader.setResource(getResource(INPUT_FILE_PATH));
        flatFileItemReader.setLineMapper(defaultLineMapper);
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setStrict(false);
        return flatFileItemReader;

    }

    private Resource getResource(String path) {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
        try {
            return resolver.getResources("file:" + path)[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("no resource found for path: " + path);
        }
    }

    @Bean(name = "translateReviewsJob")
    public Job job() {
        return jobBuilders
                .get("translateReviewsJob")
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    @JobScope
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(CONCURRENT_LIMIT);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @Bean
    public Step step1() {
        return stepBuilders
                .get("step1")
                .<Review, Review> chunk(100)
                .reader(reviewReader())
                .processor(reviewProcessor())
                .writer(reviewWriter())
                .taskExecutor(taskExecutor())
                .throttleLimit(CONCURRENT_LIMIT)
                .build();
    }
}
