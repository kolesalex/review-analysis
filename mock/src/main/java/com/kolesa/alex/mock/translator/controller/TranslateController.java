package com.kolesa.alex.mock.translator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslateController {
    
    @RequestMapping(value = "/translate", consumes = { "application/json" }, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> translate(@RequestBody Dummy request) {
        return new ResponseEntity<>("{\"text\": \"Bonjour John, comment allez-vous?\"}", HttpStatus.OK);
    }

    public static class Dummy {
        public Dummy() {
        }
    }
    
}
