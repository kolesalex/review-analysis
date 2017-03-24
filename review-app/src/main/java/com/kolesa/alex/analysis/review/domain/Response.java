package com.kolesa.alex.analysis.review.domain;

public class Response {

    private String text;

    public Response() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Response(String text) {
        this.text = text;
    }
}
