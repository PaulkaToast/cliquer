package com.styxxco.cliquer.web;

import com.styxxco.cliquer.database.AccountRepository;
import com.styxxco.cliquer.database.MessageRepository;
import com.styxxco.cliquer.database.SkillRepository;
import com.styxxco.cliquer.database.GroupRepository;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.ArrayList;

@Controller
public class RestController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private GroupRepository groupRepository;


    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/api/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProfile(@RequestParam(value="user", required=true) String username, Model model) {
        return null;
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
