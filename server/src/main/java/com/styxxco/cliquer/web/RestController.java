package com.styxxco.cliquer.web;

import com.styxxco.cliquer.database.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestController {

    @Autowired
    private AccountRepository accountRepository;


    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/api/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProfile() {
        //TODO: get profile information model
        return null;
    }

}
