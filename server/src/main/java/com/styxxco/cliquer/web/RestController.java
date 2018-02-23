package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.security.FirebaseFilter;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@Log4j
@Controller
public class RestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private FirebaseService firebaseService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String index() {
        log.info("Index called");
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody Object register(@RequestHeader(value = FirebaseFilter.HEADER_NAME) String firebaseToken, @RequestParam(value ="first") String first, @RequestParam(value="last") String last) {
        log.info("Register called");
        Account a = firebaseService.registerUser(firebaseToken, first, last);
        return getUserProfile(a.getUsername(), "user");
    }

    @RequestMapping(value = "/api/getProfile", method = RequestMethod.GET)
    public @ResponseBody Object getUserProfile(@RequestParam(value="identifier") String identifier,
                                               @RequestParam(value="type") String type) {
        Account user;
        switch (type) {
            case "user":
                user = accountService.getUserProfile(identifier);
                break;
            case "member":
                ObjectId memberID = new ObjectId(identifier);
                user = accountService.getMemberProfile(memberID);
                break;
            case "public":
                ObjectId publicID = new ObjectId(identifier);
                user = accountService.getPublicProfile(publicID);
                break;
            default:
                return HttpStatus.BAD_REQUEST;
        }
        if (user == null) {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "/api/updateProfile", method = RequestMethod.POST)
    public @ResponseBody Object updateProfile(@RequestParam(value="username") String username,
                                              @RequestParam(value="key") String key,
                                              @RequestParam(value="value") String value) {
        Account user = accountService.updateUserProfile(username, key, value);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "/api/deleteProfile", method = RequestMethod.POST)
    public @ResponseBody Object deleteAccount(@RequestParam(value="username") String username) {
        log.info("Skill list called");
        String success = accountService.deleteAccount(username);
        if (success == null) {
            return HttpStatus.BAD_REQUEST;
        }
        log.info(success);
        return success;
    }

    @RequestMapping(value = "/api/addFriend", method = RequestMethod.POST)
    public @ResponseBody Object addFriend(@RequestParam(value="username") String username, @RequestParam(value="friend") String friend) {

        return null;
    }

    /* TODO: removeFriend */

    /* TODO: createGroup */

    /* TODO: deleteGroup */

    /* TODO: inviteToGroup */

    /* TODO: leaveGroup */

    /* TODO: getUserGroups */

    @RequestMapping(value = "/api/getSkillList", method = RequestMethod.GET)
    public @ResponseBody Object getSkillList() {
        ArrayList<Skill> skills = accountService.getAllValidSkills();
        if(skills == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return skills;
    }

    @RequestMapping(value = "/api/getSkills", method = RequestMethod.GET)
    public @ResponseBody Object getSkills(@RequestParam(value="username") String username) {
        ArrayList<Skill> skills = accountService.getAllUserSkills(username);
        if(skills == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return skills;
    }

    @RequestMapping(value = "/api/addSkill", method = RequestMethod.POST)
    public @ResponseBody Object addSkill(@RequestParam(value="username") String username,
                                         @RequestParam(value="name") String skillName,
                                         @RequestParam(value="level") String skillLevel) {
        Account user = accountService.addSkill(username, skillName, skillLevel);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "/api/removeSkill", method = RequestMethod.POST)
    public @ResponseBody Object removeSkill(@RequestParam(value="username") String username,
                                            @RequestParam(value="name") String skillName) {
        Account user = accountService.removeSkill(username, skillName);
        if(user == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return user;
    }

    @RequestMapping(value = "/api/getMessages", method = RequestMethod.GET)
    public @ResponseBody Object getMessages(@RequestParam(value="username") String username) {
        ArrayList<Message> messages = accountService.getNewMessages(username);
        if(messages == null)
        {
            return HttpStatus.BAD_REQUEST;
        }
        return messages;
    }
}
