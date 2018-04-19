package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.service.AccountService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Log4j
@Controller
public class SocketController {

    @Autowired
    private AccountService accountService;

    @MessageMapping("/{userId}/{groupId}/sendMessage")
    public void send(@DestinationVariable String userId,
                     @DestinationVariable String groupId, String content) {
        System.out.println(content);
        accountService.sendMessage(userId, groupId, content, Message.Types.CHAT_MESSAGE);
    }

    @MessageMapping("/{userId}/{groupId}/messageHistory")
    public void messageHistory(@DestinationVariable String groupId,
                               @DestinationVariable String userId) {
        accountService.getChatHistory(groupId, userId);
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

    // TODO: Account for change on front end
    @MessageMapping("/{userId}/{includeRead}/{startDate}/allMessages")
    public void getAllMessages(@DestinationVariable String userId,
                               @DestinationVariable String includeRead,
                               @DestinationVariable String startDate) {
        accountService.getMessages(userId, includeRead, startDate);
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
        accountService.deleteMessage(userId, messageId);
    }

    @MessageMapping("readNotification/{userId}/{messageId}")
    public void readNotification(@DestinationVariable String userId,
                                 @DestinationVariable String messageId) {
        accountService.readMessage(userId, messageId);
    }

}
