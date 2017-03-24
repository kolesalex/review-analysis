package com.kolesa.alex.analysis.review;

import java.time.Duration;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.kolesa.alex.analysis.review.config.BatchConfig;
import com.kolesa.alex.analysis.review.config.SparkConfig;
import com.kolesa.alex.analysis.review.service.ReviewService;

public class Main {

    public static final String TRANSLATE_FLAG = "translate=true";

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SparkConfig.class);
        context.register(BatchConfig.class);
        context.refresh();

        ReviewService service = (ReviewService) context.getBean("reviewService");

        System.out.println("1) Finding 1000 most active users (profile names)");
        System.out.println(service.getMostActiveUsers(1000));

        System.out.println("2) Finding 1000 most commented food items (item ids).");
        System.out.println(service.getMostCommentedProducts(1000));

        System.out.println("3) Finding 1000 most used words in the reviews");
        System.out.println(service.getMostUsedWords(1000));

        if (args != null && args.length > 0 && TRANSLATE_FLAG.equals(args[0])) {
            step4(context);
        }


    }

    private static void step4(AnnotationConfigApplicationContext context) {
        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("translateReviewsJob");
        System.out.println("Starting the batch job");
        try {
            JobExecution execution = jobLauncher
                    .run(job, new JobParameters(ImmutableMap.of("date", new JobParameter(new Date()))));
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed at: "
                    + Duration.between(execution.getStartTime().toInstant(), execution.getEndTime().toInstant()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
    }


}
