
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import lombok.Generated;
import org.springframework.data.annotation.Id;

@lombok.Getter
@lombok.ToString(exclude = {"messageID", "type"})

public class Message
{
	@Id
	@Generated
	private String messageID;

	private final String content;		/* The actual message in the Message		*/
	private final String senderID;		/* MongoDB ID of entity that sent message	*/

	private final String type;			/* Dictates behavior on user interation		*/

	/* Types are:
	 * - "Group Invite"
	 * - "Friend Invite"
	 * - "Moderator Flag"
	 * - "Group Notification"
	 * - "Profile Notification"
	 */

	public Message(String content, String senderID, String type)
	{
		this.content = content;
		this.senderID = senderID;
		this.type = type;
	}
}

