package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/chat/{groupId}/sendMessage")
    @SendTo("/group/{groupId}")
    public Message send(@DestinationVariable String groupId, Message msg) throws Exception {
        return msg;
    }

    @MessageMapping("/chat/info")
    public Message info(Message msg) throws Exception {
        return msg;
    }

    @MessageMapping("/chat/{groupId}/getMessages")
    @SendTo("/group/messages")
    public Message get(Message msg) throws Exception {
        return msg;
    }
}
