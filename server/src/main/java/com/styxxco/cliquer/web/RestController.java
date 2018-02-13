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
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            return "invalid request";
        }
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("reputation", user.getReputation());
        model.addAttribute("skills", user.getSkills());
        ArrayList<String> friendNames = new ArrayList<>();
        ArrayList<Integer> friendReps = new ArrayList<>();
        for(String id : user.getFriendIDs())
        {
            Account friend = accountRepository.findByAccountID(id);
            if(friend.getFirstName() != null && friend.getLastName() != null)
            {
                friendNames.add(friend.getFirstName() + " " + friend.getLastName());
            }
            else
            {
                friendNames.add(friend.getUsername());
            }
            friendReps.add(friend.getReputation());
        }
        ArrayList<String> messages = new ArrayList<>();
        for(String id : user.getMessageIDs())
        {
            Message message = messageRepository.findByMessageID(id);
            messages.add(message.getContent());
        }
        model.addAttribute("friendNames", friendNames);
        model.addAttribute("friendReps", friendReps);
        model.addAttribute("messages", messages);
        return "success";
    }

    @RequestMapping(value = "/api/profile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String editProfile(@RequestParam(value="user") String username, String field, String edit) {
        Account user = accountRepository.findByUsername(username);
        switch(field)
        {
            case "firstName" : user.setFirstName(edit); break;
            case "lastName" : user.setLastName(edit); break;
            case "isPublic" : user.setPublic(Boolean.parseBoolean(edit)); break;
            case "reputationReq" : user.setReputationReq(Double.parseDouble(edit)); break;
            case "proximityReq" : user.setProximityReq(Integer.parseInt(edit)); break;
            default: return "invalid request";
        }
        accountRepository.save(user);
        return "success";
    }

    @RequestMapping(value = "/api/profile/skills", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String editSkills(@RequestParam(value="user") String username, String skillName, String option) {
        Account user = accountRepository.findByUsername(username);
        Skill skill = skillRepository.findBySkillName(skillName);
        if(skill == null)
        {
            return "invalid skill";
        }
        if(option.equals("add"))
        {
            if(user.getSkill(skillName) == null)
            {
                user.addSkill(new Skill(skillName, 0));
            }
            else
            {
                return "user already has skill";
            }
        }
        else if(option.equals("remove"))
        {
            if(user.getSkill(skillName) != null)
            {
                user.removeSkill(skillName);
            }
            else
            {
                return "user does not have skill";
            }
        }
        else
        {
            return "invalid request";
        }
        accountRepository.save(user);
        return "success";
    }

    @RequestMapping(value = "/api/group", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findGroups(@RequestParam(value="group") String groupName, Model model) {
        ArrayList<Group> groups = groupRepository.findByGroupName(groupName);
        ArrayList<String> groupNames = new ArrayList<>();
        ArrayList<String> groupPurposes = new ArrayList<>();
        for(Group g : groups)
        {
            if(g.isPublic())
            {
                groupNames.add(g.getGroupName());
                groupPurposes.add(g.getGroupPurpose());
            }
        }
        model.addAttribute(groupNames);
        model.addAttribute(groupPurposes);
        return "success";
    }

}
