
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ToString(exclude = {"messageID", "type"})

public class Message
{
	@Id
	private final String messageID;

	private final String content;		/* The actual message in the Message		*/
	private final String senderID;	/* MongoDB ID of entity that sent message	*/

	private final int type;			/* Dictates behavior on user interation		*/
	private final LocalTime creationTime;
	private final LocalDate creationDate;

	@Setter
	private String groupID;	/* MongoDB ID of group that message refers to, if applicable	*/
	@Setter
	private boolean read;

	public static class Types {
		public static final int GROUP_INVITE = 0;
		public static final int FRIEND_INVITE = 1;
		public static final int MOD_FLAG = 2;
		public static final int GROUP_NOTIFICATION = 3;
		public static final int PROFILE_NOTIFICATION = 4;
		public static final int JOIN_REQUEST = 5;
		public static final int CHAT_MESSAGE = 6;
	}

	public Message(String senderID, String content, int type)
	{
		this.messageID = new ObjectId().toString();
		this.senderID = senderID;
		this.content = content;
		this.type = type;
		this.creationTime = LocalTime.now();
		this.creationDate = LocalDate.now();
		this.groupID = null;
		this.read = false;
	}
}

