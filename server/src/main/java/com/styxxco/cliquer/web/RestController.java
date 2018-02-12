package com.styxxco.cliquer.web;

import com.styxxco.cliquer.database.AccountRepository;
import com.styxxco.cliquer.database.MessageRepository;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
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


    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/api/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProfile(@RequestParam(value="user", required=true) String username, Model model) {
        //TODO: get profile information model

        Account user = accountRepository.findByUsername(username);
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
        return "profile";
    }

}
