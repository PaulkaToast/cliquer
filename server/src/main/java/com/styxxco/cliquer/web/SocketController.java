package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.ChatMessage;
import com.styxxco.cliquer.domain.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/{groupID}/sendMessage")
    @SendTo("/group/{groupID}/message")
    public ChatMessage send(@DestinationVariable String groupID, ChatMessage msg) throws Exception {
        return msg;
    }
}
