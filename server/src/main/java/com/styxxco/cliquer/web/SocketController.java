package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.ChatMessage;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Log4j
@Controller
public class SocketController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GroupService groupService;

    @MessageMapping("/{groupId}/sendMessage")
    @SendTo("/group/{groupId}")
    public ChatMessage send(@DestinationVariable String groupId, ChatMessage msg) {
        Account a = accountService.getUserProfile(msg.getSenderId());
        if (a == null) {
            log.info("Account ID not found for User: " + msg.getSenderId());
            return null;
        }
        msg.setSenderName(a.getFirstName() + " " + a.getLastName());

        Group group = groupService.getUserGroup(groupId, a.getAccountID());
        if (group == null) {
            log.info("No group found for groupId: " + groupId + " and User: " + msg.getSenderId());
            return null;
        }

        groupService.sendChatMessage(msg, group.getGroupID());
        return msg;
    }

    @MessageMapping("/{username}/{groupId}/messageHistory")
    @SendTo("/group/{username}/{groupId}")
    public List<ChatMessage> messageHistory(@DestinationVariable String groupId,
                                            @DestinationVariable String username) {
        Account a = accountService.getUserProfile(username);
        if (a == null) {
            log.info("Account ID not found for User: " + username);
            return null;
        }
        Group group = groupService.getUserGroup(groupId, a.getAccountID());

        return group.getChatHistory();
    }

    @MessageMapping("/{userId}/{groupId}/rate")
    @SendTo("/group/{groupId}")
    public Message requestRate(@DestinationVariable String groupId,
                               @DestinationVariable String userId) {
        Message message = accountService.requestRating(userId, groupId);
        if (message == null) {
            log.info("Could not send rate request for group " + groupId);
        }
        return message;
    }

    @MessageMapping("/inviteToGroup/{userId}/{friendId}/{groupId}")
    @SendTo("/notification/{friendId}")
    public Message inviteToGroup(@DestinationVariable String friendId,
                                 String userId,
                                 String groupId) {
        Message invite = accountService.inviteToGroup(userId, friendId, groupId);
        if (invite == null) {
            log.info("Could not invite user " + friendId + " to group " + groupId);
        }
        return invite;
    }

    @MessageMapping("/requestToGroup/{userId}/{leaderId}{groupId}")
    @SendTo("/notification/{leaderId}")
    public Message requestToGroup(@DestinationVariable String leaderId,
                                  String userId,
                                  String groupId) {
        Message message = accountService.requestToGroup(userId, leaderId, groupId);
        if (message == null) {
            log.info("Could not send request to group " + groupId);
        }
        return message;
    }

    @MessageMapping("requestFriend/{userId}/{friendId}")
    @SendTo("/notification/{friendId}")
    public Message requestFriend(@DestinationVariable String friendId,
                                 String userId) {
        Message invite = accountService.sendFriendInvite(userId, friendId);
        if (invite == null) {
            log.info("Could not send a friend request to " + friendId);
        }
        return invite;
    }

    @MessageMapping("/{userId}/allMessages")
    @SendTo("/notification/{userId}")
    public List<Message> getAllMessages(@DestinationVariable String userId) {
        List<Message> list = accountService.getNewMessages(userId);
        if (list == null) {
            log.info("Could not get notifications for user " + userId);
        }
        return list;
    }
}
