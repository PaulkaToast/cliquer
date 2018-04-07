
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@ToString(exclude = {"messageID", "type"})

public class Message
{
	@Id
	private final String messageID;
	private final String senderName;

	private final String content;		/* The actual message in the Message		*/
	private final String senderID;	/* MongoDB ID of entity that sent message	*/

	private final int type;			/* Dictates behavior on user interation		*/

	private LocalTime creationTime;
	private LocalDate creationDate;

	private String groupID;			/* ID of group that message refers to, if applicable	*/
	private String accountID;		/* ID of account that message refers to, if applicable	*/
	private String chatMessageID;	/* ID of chat message that message refers to, if applicable	*/
	private boolean read;

	@JsonIgnore
	private String parentID; /* ID if multiple messages belong to a particular action */
	@JsonIgnore
	private int counter; /* counter for votes */

    private Map<String, Integer> reactions;

	public static class Types {
		public static final int GROUP_INVITE = 0;
		public static final int FRIEND_INVITE = 1;
		public static final int MOD_FLAG = 2;
		public static final int KICKED = 3;
		public static final int JOIN_REQUEST = 4;
		public static final int RATE_REQUEST = 5;
		public static final int GROUP_ACCEPTED = 6;
		public static final int FRIEND_ACCEPTED = 7;
		public static final int EVENT_INVITE = 8;
		public static final int MOD_REQUEST = 9;
		public static final int MOD_ACCEPTED = 10;
		public static final int MOD_INVITE = 11;
		public static final int SEARCH_INVITE = 12;
		public static final int CHAT_MESSAGE = 13;
		public static final int REPORT = 13;
	}

    public static class Reactions {
        public static final int UP_VOTE = 0;
        public static final int DOWN_VOTE = 1;
    }

	public Message(String senderID, String senderName, String content, int type) {
		this.messageID = new ObjectId().toString();
		this.senderID = senderID;
		this.senderName = senderName;
		this.content = content;
		this.type = type;
		this.creationTime = LocalTime.now();
		this.creationDate = LocalDate.now();
		this.groupID = null;
		this.accountID = null;
		this.chatMessageID = null;
		this.read = false;
		this.parentID = null;
		this.reactions = new TreeMap<>();
	}

	public void addReaction(String accountID, int reaction)
    {
        reactions.put(accountID, reaction);
    }

    public void removeReaction(String accountID)
    {
        reactions.remove(accountID);
    }

    public int getReaction(String accountID)
	{
		if(!reactions.containsKey(accountID)) {
			return -1;
		}
		return reactions.get(accountID);
	}

    public boolean hasReaction(String accountID) {
		return reactions.containsKey(accountID);
	}

	public void increment() {
		counter++;
	}

	public void decrement() {
		counter--;
	}
}

