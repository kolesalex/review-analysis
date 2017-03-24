package com.kolesa.alex.analysis.review.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kolesa.alex.analysis.review.config.SparkConfig;

import scala.Tuple2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SparkConfig.class)
public class ReviewServiceTest {

    public static final String TEST_REVIEWS_CSV = "src/test/resources/test_reviews.csv";

    @Autowired
    private ReviewService reviewService;

    @Before
    public  void before(){
        reviewService.setResourcePath(TEST_REVIEWS_CSV);
    }

    @Test
    public void test_getMostActiveUsers(){
        List<Tuple2<Integer,String>> result = reviewService.getMostActiveUsers(1000);
        assertThat(result.get(0)._1, is(2));
        assertThat("max ", result.get(0)._2, is("delmartian"));
    }

    @Test
    public void test_getMostCommentedProducts(){
        List<Tuple2<Integer,String>> result = reviewService.getMostCommentedProducts(1000);

        assertThat(result.get(0)._1, is(4));
        assertThat(result.get(0)._2, is("B006K2ZZ7K"));
    }

    @Test
    public void test_getMostUsedWords(){
        List<Tuple2<Integer,String>> result = reviewService.getMostUsedWords(1000);

        assertThat(result.get(0)._1, is(10));
        assertThat(result.get(0)._2, is("the"));

        assertThat(result.get(1)._1, is(10));
        assertThat(result.get(1)._2, is("a"));
    }
}