package com.styxxco.cliquer.web;

import com.styxxco.cliquer.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    @MessageMapping("/chat")
    @SendTo("/group/messages")
    public Message send(Message msg) throws Exception {
        return msg;
    }
}
