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
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
@Controller
public class SocketController {

    @Autowired
    private AccountService accountService;

    @MessageMapping("/{groupId}/sendMessage")
    public void send(@DestinationVariable String groupId, ChatMessage msg) {
        accountService.sendChatMessageFromUser(groupId, msg);
    }

    @MessageMapping("/{username}/{groupId}/messageHistory")
    public void messageHistory(@DestinationVariable String groupId,
                               @DestinationVariable String username) {
        accountService.getChatHistory(groupId, username);
    }

    @MessageMapping("/{userId}/{groupId}/rate")
    public void requestRate(@DestinationVariable String groupId,
                            @DestinationVariable String userId) {
        accountService.requestRating(userId, groupId);
    }

    @MessageMapping("/inviteToGroup/{userId}/{friendId}/{groupId}")
    public void inviteToGroup(@DestinationVariable String friendId,
                              @DestinationVariable String userId,
                              @DestinationVariable String groupId) {
        accountService.inviteToGroup(userId, friendId, groupId);
    }

    @MessageMapping("/requestToJoin/{userId}/{leaderId}/{groupId}")
    public void requestToJoin(@DestinationVariable String userId,
                              @DestinationVariable String leaderId,
                              @DestinationVariable String groupId) {
        accountService.requestToGroup(userId, leaderId, groupId);
    }

    @MessageMapping("requestFriend/{userId}/{friendId}")
    public void requestFriend(@DestinationVariable String friendId,
                                 @DestinationVariable String userId) {
        accountService.sendFriendInvite(userId, friendId);
    }

    @MessageMapping("/{userId}/allMessages")
    public void getAllMessages(@DestinationVariable String userId) {
        accountService.getNewMessages(userId);
    }

    @MessageMapping("acceptNotification/{userId}/{messageId}")
    public void acceptNotification(@DestinationVariable String userId,
                                   @DestinationVariable String messageId) {
        accountService.handleAcceptNotification(userId, messageId);
    }

    @MessageMapping("rejectNotification/{userId}/{messageId}")
    public void rejectNotification(@DestinationVariable String userId,
                                   @DestinationVariable String messageId) {
        accountService.handleRejectNotification(userId, messageId);
    }

    @MessageMapping("deleteNotification/{userId}/{messageId}")
    public void deleteNotification(@DestinationVariable String userId,
                                   @DestinationVariable String messageId) {

    }

    @MessageMapping("readNotification/{userId}/{messageId}")
    public void readNotification(@DestinationVariable String userId,
                                 @DestinationVariable String messageId) {

    }

}
