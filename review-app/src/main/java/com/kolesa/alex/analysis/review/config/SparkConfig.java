package com.kolesa.alex.analysis.review.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.kolesa.alex.analysis.review.service")
public class SparkConfig {

    @Bean
    public JavaSparkContext sc(){
        SparkConf conf = new SparkConf()
                .setAppName("review analyst")
                .setMaster("local[*]");

        return new JavaSparkContext(conf);
    }

}
