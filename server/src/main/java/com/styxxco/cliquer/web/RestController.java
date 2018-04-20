package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseFilter;
import lombok.extern.log4j.Log4j;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    private final String OKAY = "{\"status\": \"OK\"}";

    @RequestMapping(value = "/", method = RequestMethod.GET)
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
//            long served = user.getStartSuspendTime().until(LocalDateTime.now(), MINUTES);
//            long timeLeft = user.getSuspendTime() - served;
//            String body = "{\"status\": \"" + user.getFullName() + " 's account is disabled until " + LocalDateTime.now().plusMinutes(timeLeft) + "\"}";
//            System.out.println(body);
//            return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/deleteProfile", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> deleteAccount(@RequestParam(value = "username") String username) {
        Account success = accountService.deleteAccount(username);
        if (success == null) {
            return new ResponseEntity<>("Could not delete profile", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/removeFriend", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> removeFriend(@RequestParam(value = "userId") String userId,
                                                        @RequestParam(value = "friend") String friend) {
        Account account = accountService.removeFriend(userId, friend);
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
        Message message = accountService.kickMember(userId, kickedId, groupId);
        if (message == null) {
            return new ResponseEntity<>("Could not kick member from group", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/startKickVote", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> startKickVote(@RequestParam(value = "userId") String userId,
                                                         @RequestParam(value = "kickedId") String kickedId,
                                                         @RequestParam(value = "groupId") String groupId) {
        accountService.startKickVote(userId, kickedId, groupId);
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
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
    public @ResponseBody ResponseEntity<?> rateUser(@RequestParam(value = "userId") String userId,
                                                    @RequestParam(value = "rateeId") String rateeId,
                                                    @RequestParam(value = "groupId") String groupId,
                                                    @RequestParam(value = "endorse", required = false, defaultValue = "false") boolean endorse,
                                                    @RequestBody String json) {
        Account user = accountService.rateUser(userId, rateeId, groupId, json, endorse);
        if (user == null) {
            return new ResponseEntity<>("Could not rate user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/search", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> search(@RequestParam(value = "userId") String userId,
                                                  @RequestParam(value = "type") String type,
                                                  @RequestParam(value = "query") String query) {
        System.out.println(type + " : " + query);
        Map<String, ? extends Searchable> map = accountService.searchWithFilter(userId, type, query);

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

    @RequestMapping(value = "/api/getRateForm", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> getRateForm(@RequestParam(value = "userId") String userId,
                                                      @RequestParam(value = "rateeId") String rateeId,
                                                      @RequestParam(value = "groupId") String groupId) {
        Map<String, Integer> map = accountService.getRateForm(userId, rateeId, groupId);
        if (map == null) {
            return new ResponseEntity<>("Could not get rate form correctly", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/reportUser", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> reportUser(@RequestParam(value = "userId") String userId,
                                                      @RequestParam(value = "reporteeId") String reporteeId,
                                                      @RequestParam(value = "reason") String reason) {
        Message message = accountService.reportUser(userId, reporteeId, reason);
        if(message == null){
            return new ResponseEntity<>("Could not report user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/reportMember", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> reportMember(@RequestParam(value = "userId") String userId,
                                                        @RequestParam(value = "groupId") String groupId,
                                                        @RequestParam(value = "messageId") String messageId,
                                                        @RequestBody String reason) {
        Message message = accountService.reportGroupMember(groupId, userId, messageId, reason);
        if(message == null){
            return new ResponseEntity<>("Could not report group member", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/applyForMod", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> applyForMod(@RequestParam(value = "userId") String userId,
                                                       @RequestParam(value = "messageId") String messageId,
                                                       @RequestBody String reason) {
        System.out.println("APPLY FOR MOD REASON: " + reason);
        Message message = accountService.acceptModInvite(userId, messageId, reason);
        if (message == null) {
            return new ResponseEntity<>("Could not apply for moderator", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/flagUser", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> flagUser(@RequestParam(value = "modId") String modId,
                                                    @RequestParam(value = "messageId") String messageId) {
        Account user = accountService.flagUser(modId, messageId);
        if(user == null){
            return new ResponseEntity<>("Could not flag user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/suspendUser", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> suspendUser(@RequestParam(value = "modId") String modId,
                                                       @RequestParam(value = "messageId") String messageId) {
        Account user = accountService.suspendUser(modId, messageId);
        if(user == null){
            return new ResponseEntity<>("Could not suspend user", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/activityLog", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> activityLog(@RequestParam(value = "modId") String modId,
                                                       @RequestParam(value = "userId") String userId,
                                                       @RequestParam(value = "startDate", required = false) String startDate,
                                                       @RequestParam(value = "endDate", required = false) String endDate) {
        List<String> log = accountService.getActivityLog(modId, userId, startDate, endDate);
        if (log == null) {
            return new ResponseEntity<>("Could not retrieve logs", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/chatContext", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> chatContext(@RequestParam(value = "modId") String modId,
                                                       @RequestParam(value = "messageId") String messageId,
                                                       @RequestParam(value = "startId", required = false) String startId,
                                                       @RequestParam(value = "endId", required = false) String endId) {
        List<Message> log = accountService.getReportContext(modId, messageId, startId, endId);
        if (log == null) {
            return new ResponseEntity<>("Could not retrieve chat context", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/addSkill", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> addSkill(@RequestParam(value = "modId") String modId,
                                                    @RequestParam(value = "skillName") String skillName) {
        Skill skill = accountService.addSkillToDatabase(modId, skillName);
        if (skill == null) {
            return new ResponseEntity<>("Could not add skill to database", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/editUser", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> editUser(@RequestParam(value = "modId") String modId,
                                                   @RequestParam(value = "userId") String userId,
                                                   @RequestParam(value = "field") String field,
                                                   @RequestParam(value = "value") String value) {
        Account user = accountService.editUserProfile(modId, userId, field, value);
        if (user == null) {
            return new ResponseEntity<>("Could not modify account", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/react", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> react(@RequestParam(value = "userId") String userId,
                                                 @RequestParam(value = "groupId") String groupId,
                                                 @RequestParam(value = "messageId") String messageId,
                                                 @RequestParam(value = "reaction") String reaction) {
        Message message = accountService.reactToChatMessage(groupId, userId, messageId, reaction);
        if (message == null) {
            return new ResponseEntity<>("Could not react to chat message", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/createEvent", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> createEvent(@RequestParam(value = "groupId") String groupId,
                                                       @RequestBody String json) {
        Group group = accountService.createEvent(groupId, json);
        if (group == null) {
            return new ResponseEntity<>("Could not create event", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/inviteAll", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> inviteAll(@RequestParam(value = "userId") String userId,
                                                     @RequestParam(value = "groupId") String groupId) {
        List<Account> accounts = accountService.inviteAll(userId, groupId);
        if (accounts == null) {
            return new ResponseEntity<>("Could not invite users", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/uploadFile", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> uploadFile(@RequestParam(value = "userId") String userId,
                                                      @RequestBody String encoding) {
        try {
            accountService.uploadPicture(userId, encoding);
        } catch (Exception e) {
            log.info("Could not upload image properly");
            return new ResponseEntity<>("Could not upload image properly", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Profile picture uploaded successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/api/setLocation", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> setLocation(@RequestParam(value = "userId") String userId,
                                                          @RequestParam(value = "latitude") String latitude,
                                                          @RequestParam(value = "longitude") String longitude) {
        accountService.setLocation(userId, latitude, longitude);
        return new ResponseEntity<>(OKAY, HttpStatus.OK);
    }

    @RequestMapping(value = "/mod/submitSkill", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> submitSkill(@RequestParam(value = "modId") String modId,
                                                       @RequestParam(value = "skillName") String skillName) {
        Skill skill = accountService.addSkillToDatabase(skillName);
        if (skill == null) {
            return new ResponseEntity<>("Could not add new skill", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(skill, HttpStatus.OK);
    }

}
