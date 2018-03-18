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

    @MessageMapping("/{groupID}/sendMessage")
    @SendTo("/group/{groupID}/message")
    public ChatMessage send(@DestinationVariable String groupID, ChatMessage msg) {
        Account a = accountService.getUserProfile(msg.getSenderId());
        if (a == null) {
            log.info("Account ID not found for User: " + msg.getSenderId());
            return null;
        }
        msg.setSenderName(a.getFirstName() + " " + a.getLastName());

        Group group = groupService.getUserGroup(new ObjectId(groupID), a.getAccountID());
        if (group == null) {
            log.info("No group found for groupID: " + groupID + " and User: " + msg.getSenderId());
            return null;
        }

        groupService.sendChatMessage(msg, group.getGroupID());

        return msg;
    }

    @MessageMapping("/{username}/{groupID}/messageHistory")
    @SendTo("/group/{username}/{groupID}")
    public List<ChatMessage> messageHistory(@DestinationVariable String groupID, @DestinationVariable String username) {
        Account a = accountService.getUserProfile(username);
        if (a == null) {
            log.info("Account ID not found for User: " + username);
            return null;
        }
        Group group = groupService.getUserGroup(new ObjectId(groupID), a.getAccountID());

        return group.getChatHistory();
    }
}
