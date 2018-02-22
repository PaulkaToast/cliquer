
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

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
	private final ObjectId messageID;

	private final String content;		/* The actual message in the Message		*/
	private final ObjectId senderID;	/* MongoDB ID of entity that sent message	*/

	private final int type;			/* Dictates behavior on user interation		*/
	private final LocalTime creationTime;
	private final LocalDate creationDate;

	@Setter
	private boolean read;

	/* Types are:
	 * 0 - Group Invite
	 * 1 - Friend Invite
	 * 2 - Moderator Flag
	 * 3 - Group Notification
	 * 4 - Profile Notification
	 */

	public Message(ObjectId senderID, String content, int type)
	{
		this.messageID = new ObjectId();
		this.senderID = senderID;
		this.content = content;
		this.type = type;
		this.creationTime = LocalTime.now();
		this.creationDate = LocalDate.now();
		this.read = false;
	}
}

