package com.kolesa.alex.analysis.review.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kolesa.alex.analysis.review.config.BatchConfig;

import scala.Tuple2;

@Service
public class ReviewService {

    @Autowired
    private JavaSparkContext sparkContext;

    private String resourcePath;

    @PostConstruct
    private void init(){
        resourcePath = BatchConfig.INPUT_FILE_PATH;
    }

    public JavaRDD<String[]> getRddFromFile(String path){
        return sparkContext
                .textFile(path)
                .map(line -> line.split(","));
    }

    public List<Tuple2<Integer,String>> getMostActiveUsers(int topSize){
        return getRddFromFile(getResourcePath())
                .mapToPair(array -> new Tuple2<>(array[3], 1))
                .reduceByKey((a,b) -> a + b)
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .take(topSize);
    }

    public List<Tuple2<Integer,String>> getMostCommentedProducts(int topSize){
        return getRddFromFile(getResourcePath())
                .mapToPair(array -> new Tuple2<>(array[1], 1))
                .reduceByKey((a,b) -> a + b)
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .take(topSize);
    }

    public List<Tuple2<Integer,String>> getMostUsedWords(int topSize){
        return getRddFromFile(getResourcePath())
                .map(array -> array[9])
                .flatMap(line -> Arrays.asList(line.split("\\s+")))
                .map(word -> word.replaceAll("[^\\w]", "").toLowerCase())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a,b) -> a + b)
                .mapToPair(Tuple2::swap)
                .sortByKey(false)
                .take(topSize);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
