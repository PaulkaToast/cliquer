package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class ChatMessage {

    @Getter
    private String content;
    @Getter
    private String senderId;

    @Getter
    private String senderName;

    public ChatMessage() {
    }

    public ChatMessage(@JsonProperty("content") String content,
                        @JsonProperty("senderId") String senderId) {
        this(content, senderId, null);
    }

    public ChatMessage( String content, String senderId, String senderName) {
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
    }

}
