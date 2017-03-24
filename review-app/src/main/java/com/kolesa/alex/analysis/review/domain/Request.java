package com.kolesa.alex.analysis.review.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    private final String input_lang = "en";
    private final String output_lang = "fr";

    private String text;

    public Request(String text){
        this.text = text;
    }

    public String getInput_lang() {
        return input_lang;
    }

    public String getOutput_lang() {
        return output_lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
