
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
	private final String senderID;		/* MongoDB ID of entity that sent message	*/

	private final String type;			/* Dictates behavior on user interation		*/
	private final LocalTime creationTime;
	private final LocalDate creationDate;

	@Setter
	private boolean read;

	/* Types are:
	 * - "Group Invite"
	 * - "Friend Invite"
	 * - "Moderator Flag"
	 * - "Group Notification"
	 * - "Profile Notification"
	 */

	public Message(String content, String senderID, String type)
	{
		this.messageID = new ObjectId();
		this.content = content;
		this.senderID = senderID;
		this.type = type;
		this.creationTime = LocalTime.now();
		this.creationDate = LocalDate.now();
		this.read = false;
	}
}

