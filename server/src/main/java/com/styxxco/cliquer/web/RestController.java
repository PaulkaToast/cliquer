package com.styxxco.cliquer.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseFilter;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
        return getUserProfile(a.getUsername(), null,"user");
    }

    @RequestMapping(value = "/api/getProfile", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getUserProfile(@RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "userId", required = false) String userId,
                                     @RequestParam(value = "type") String type) {

        Account user = accountService.getProfile(username, userId, type);
        if (user == null) {
            return new ResponseEntity<>("Could not fetch profile with the query", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/deleteProfile", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> deleteAccount(@RequestParam(value = "username") String username) {
        Account success = accountService.deleteAccount(username);
        if (success == null) {
            return new ResponseEntity<>("Could not delete profile", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/acceptFriend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> acceptFriend(@RequestParam(value = "userId") String userId,
                                @RequestParam(value = "inviteId") String inviteId) {
        Message message = accountService.acceptFriendInvite(userId, inviteId);
        if (message == null) {
            return new ResponseEntity<>("Could not add friend", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/removeFriend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> removeFriend(@RequestParam(value = "username") String username,
                                   @RequestParam(value = "friend") String friend) {
        Account account = accountService.removeFriend(username, friend);
        if (account == null) {
            return new ResponseEntity<>("Could not remove friend", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/createGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> createGroup(@RequestParam(value = "username") String username,
                                  @RequestBody String json) {
        Group group = accountService.createGroup(username, json);
        if (group == null) {
            return new ResponseEntity<>("Could not create group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/leaveGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> leaveGroup(@RequestParam(value = "username") String username,
                                 @RequestParam(value = "groupId") String groupId) {
        Account user = accountService.leaveGroup(username, groupId);
        if (user == null) {
            return new ResponseEntity<>("Could not find group from this id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/deleteGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> deleteGroup(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "groupId") String groupId) {
        Group result = accountService.deleteGroup(username, groupId);
        if (result == null) {
            return new ResponseEntity<>("Could not delete group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/setGroupSettings", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> setGroupSettings(@RequestParam(value = "username") String username,
                                                            @RequestParam(value = "groupId") String groupId,
                                                            @RequestBody String json) {
        Group group = accountService.setGroupSettings(username, groupId, json);
        if (group == null) {
            return new ResponseEntity<>("Could not update group settings", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/addToGroup", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addToGroup(@RequestParam(value = "username") String username,
                                 @RequestParam(value = "groupId") String groupId) {
        Account user = accountService.addToGroup(username, groupId);
        if (user == null) {
            return new ResponseEntity<>("Could not find group from this id", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/kick", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> kick(@RequestParam(value = "userId") String userId,
                     @RequestParam(value = "kickedId") String kickedId,
                     @RequestParam(value = "groupId") String groupId) {
        Account user = accountService.kickMember(userId, kickedId, groupId);
        if (user == null) {
            return new ResponseEntity<>("Could not kick member from group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/getUserGroups", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getUserGroups(@RequestParam(value = "username") String username) {
        List<Group> groups = accountService.getAllUserGroups(username);
        System.out.println(groups);
        if (groups == null) {
            return new ResponseEntity<>("Could not find groups", HttpStatus.BAD_REQUEST);
        }
        Map<String, Group> map = groups.stream().collect(Collectors.toMap(Group::getGroupID, group -> group));
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
    public @ResponseBody ResponseEntity<?> getSkills(@RequestParam(value = "userId") String userId) {
        List<Skill> skills = accountService.getAllUserSkills(userId);
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

    @RequestMapping(value = "/api/rateUser", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> rateUser(@RequestParam(value = "username") String username,
                                                    @RequestParam(value = "friend") String friend,
                                                    @RequestBody String json) {
        Account user = accountService.rateUser(username, friend, json);
        if (user == null) {
            return new ResponseEntity<>("Could not rate user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> search(@RequestParam(value = "type") String type,
                                                  @RequestParam(value = "query", required = false, defaultValue = "null") String query,
                                                  @RequestParam(value = "suggestions", required = false, defaultValue = "true") boolean suggestions,
                                                  @RequestParam(value = "weights", required = false, defaultValue = "true") boolean weights) {
        Map<String, ? extends Searchable> map = accountService.searchWithFilter(type, query, suggestions, weights);
        if (map == null) {
            return new ResponseEntity<>("Could not find any results", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/setSettings", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> updateSettings(@RequestParam(value = "username") String username,
                               @RequestBody String json) {
        Account user = accountService.setAccountSettings(username, json);
        if (user == null) {
            return new ResponseEntity<>("Could not update account settings", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // TODO: handle notifications or use websockets
    @RequestMapping(value = "/api/handleNotification", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> handleNotification(@RequestParam(value = "userId") String userId,
                                                              @RequestParam(value = "messageId") String messageId,
                                                              @RequestParam(value = "accept", required = false, defaultValue = "true") boolean accept) {
        accountService.handleNotifications(userId, messageId, accept);
        return new ResponseEntity<>("{\"status\": \"cool\"}", HttpStatus.OK);
    }

    @RequestMapping(value = "/api/getRateForm", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getRateForm(@RequestParam(value = "userId") String userId,
                                                      @RequestParam(value = "rateeId") String rateeId,
                                                      @RequestParam(value = "groupId") String groupId) {
        Map<String, Integer> map = accountService.getRateForm(userId, rateeId, groupId);
        if (map == null) {
            log.info("Could not get rate form correctly");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    // TODO: reputationRank endpoint

}
