package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseFilter;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public @ResponseBody ResponseEntity<?> register(@RequestHeader(value = FirebaseFilter.HEADER_NAME) String firebaseToken,
                               @RequestParam(value = "first") String first,
                               @RequestParam(value = "last") String last) {
        Account a = firebaseService.registerUser(firebaseToken, first, last);
        return getUserProfile(a.getUsername(), "user");
    }

    @RequestMapping(value = "/api/getProfile", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getUserProfile(@RequestParam(value = "username") String username,
                                     @RequestParam(value = "type") String type) {
        Account user = accountService.getProfile(username, type);
        if (user == null) {
            return new ResponseEntity<>("Could not fetch profile with the query", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/deleteProfile", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> deleteAccount(@RequestParam(value = "username") String username) {
        String success = accountService.deleteAccount(username);
        if (success == null) {
            return new ResponseEntity<>("Could not delete profile", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/addFriend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addFriend(@RequestParam(value = "username") String username,
                                @RequestParam(value = "friend") String friend) {
        ObjectId friendID = new ObjectId(friend);
        Account account = accountService.addFriend(username, friendID);
        if (account == null) {
            return new ResponseEntity<>("Could not add friend", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    // TODO: /api/requestFriend endpoint

    @RequestMapping(value = "/api/removeFriend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> removeFriend(@RequestParam(value = "username") String username,
                                   @RequestParam(value = "friend") String friend) {
        ObjectId friendID = new ObjectId(friend);
        Account account = accountService.removeFriend(username, friendID);
        if (account == null) {
            return new ResponseEntity<>("Could not remove friend", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/createGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> createGroup(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "groupName") String groupName,
                                  @RequestParam(value = "bio") String bio) {
        Group group = accountService.createGroup(username, groupName, bio);
        if (group == null) {
            return new ResponseEntity<>("Could not create group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    // TODO: /api/updateGroup endpoint

    @RequestMapping(value = "/api/leaveGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> leaveGroup(@RequestParam(value = "username") String username,
                                 @RequestParam(value = "groupId") String groupId) {
        ObjectId groupID = new ObjectId(groupId);
        Account user = accountService.leaveGroup(username, groupID);
        if (user == null) {
            return new ResponseEntity<>("Could not find group from this id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/deleteGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> deleteGroup(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "groupId") String groupId) {
        ObjectId groupID = new ObjectId(groupId);
        String result = accountService.deleteGroup(username, groupID);
        if (result == null) {
            return new ResponseEntity<>("Could not delete group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // TODO: /api/requestGroup endpoint

    // TODO: /api/setGroupSettings endpoint

    @RequestMapping(value = "/api/addToGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addToGroup(@RequestParam(value = "username") String username,
                                 @RequestParam(value = "groupId") String groupId) {
        ObjectId groupID = new ObjectId(groupId);
        Account user = accountService.addToGroup(username, groupID);
        if (user == null) {
            return new ResponseEntity<>("Could not find group from this id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/inviteToGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> inviteToGroup(@RequestParam(value = "username") String username,
                                    @RequestParam(value = "friend") String friend,
                                    @RequestParam(value = "groupId") String groupId) {
        ObjectId accountID = new ObjectId(friend);
        ObjectId groupID = new ObjectId(groupId);
        Account user = accountService.inviteToGroup(username, accountID, groupID);
        if (user == null) {
            return new ResponseEntity<>("Could not find group from this id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // TODO: /api/kick endpoint

    @RequestMapping(value = "/api/getUserGroups")
    public @ResponseBody ResponseEntity<?> getUserGroups(@RequestParam(value = "username") String username) {
        List<Group> groups = accountService.getAllUserGroups(username);
        if (groups == null) {
            return new ResponseEntity<>("Could not find groups", HttpStatus.BAD_REQUEST);
        }
        Map<String, Group> map = groups.stream().collect(Collectors.toMap(Group::getGid, group -> group));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/getSkillList", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getSkillList() {
        List<Skill> skills = accountService.getAllValidSkills();
        if (skills == null) {
            return new ResponseEntity<>("Could not find skills", HttpStatus.BAD_REQUEST);
        }
        List<String> list = skills.stream().map(Skill::getSkillName).collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/getSkills", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getSkills(@RequestParam(value = "username") String username) {
        List<Skill> skills = accountService.getAllUserSkills(username);
        if (skills == null) {
            return new ResponseEntity<>("Could not find skills", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/addSkills", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addSkills(@RequestParam(value = "username") String username,
                                @RequestBody String json) {
        List<? extends Searchable> skills = accountService.addSkills(username, json);
        if (skills == null) {
            return new ResponseEntity<>("Could not add skills", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/removeSkill", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> removeSkill(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "name") String skillName) {
        Account account = accountService.removeSkill(username, skillName);
        if (account == null) {
            return new ResponseEntity<>("Could not remove skill", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/getMessages", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getMessages(@RequestParam(value = "username") String username) {
        List<Message> messages = accountService.getNewMessages(username);
        if (messages == null) {
            return new ResponseEntity<>("Could not find messages", HttpStatus.BAD_REQUEST);
        }
        Map<String, Message> map = messages.stream().collect(Collectors.toMap(Message::getMid, message -> message));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    // TODO: /api/getChatLog

    // TODO: /api/sendChat

    // TODO: /api/receiveChat

    // TODO: /api/requestRating

    // TODO: notification WebSocket

    // TODO: /api/rateUser

    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> search(@RequestParam(value = "type") String type,
                             @RequestParam(value = "query", required = false, defaultValue = "null") String query,
                             @RequestParam(value = "level", required = false, defaultValue = "0") int level,
                             @RequestParam(value = "suggestions", required = false, defaultValue = "true") boolean suggestions,
                             @RequestParam(value = "weights", required = false, defaultValue = "true") boolean weights) {
        Map<String, ? extends Searchable> map = accountService.searchWithFilter(type, query, level, suggestions, weights);
        if (map == null) {
            return new ResponseEntity<>("Could not find any results", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/setSettings", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> updateSettings(@RequestBody String s) {
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

}
