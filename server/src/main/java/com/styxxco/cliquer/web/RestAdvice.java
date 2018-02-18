package com.styxxco.cliquer.web;

import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Log4j
@ControllerAdvice
public class RestAdvice {

    public String handleFirebaseAuthException(SecurityException e) {
        log.info("Authentication error", e);
        return null;
    }

}
