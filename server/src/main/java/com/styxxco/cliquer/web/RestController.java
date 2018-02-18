package com.styxxco.cliquer.web;

import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class RestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FirebaseService firebaseService;

    @RequestMapping(value = "/open/index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/open/signup", method = RequestMethod.POST)
    public String signUp(@RequestHeader(value = "X-Authorization-Firebase") String firebaseToken) {
        firebaseService.registerUser(firebaseToken);
        return "greeting";
    }

    @RequestMapping(value = "/api/profile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String editProfile(@RequestParam(value="user") String username, String field, String edit) {
        return null;
    }

    @RequestMapping(value = "/api/profile/skills", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String editSkills(@RequestParam(value="user") String username, String skillName, String option) {
        return null;
    }

    @RequestMapping(value = "/api/group", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findGroups(@RequestParam(value="group") String groupName, Model model) {
        return null;
    }

}
