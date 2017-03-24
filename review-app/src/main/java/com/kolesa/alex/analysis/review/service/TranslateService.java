package com.kolesa.alex.analysis.review.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kolesa.alex.analysis.review.domain.Request;
import com.kolesa.alex.analysis.review.domain.Response;

@Service
public class TranslateService {

    public static final String GOOGLE_TRANSLATE_MOCK = "http://localhost:8080/translate";

    private RestTemplate restTemplate;

    @PostConstruct
    private void init(){
        restTemplate = new RestTemplate();
    }

    public String translate(String text){
        Response response = restTemplate.postForObject(GOOGLE_TRANSLATE_MOCK, new Request(text), Response.class);
        return response.getText();
    }

}
