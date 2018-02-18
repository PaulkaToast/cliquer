
package com.styxxco.cliquer.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
@ToString(of = {"username", "firstName", "lastName", "reputation"})

public class Account
{
	@Id
	private final ObjectId accountID;

    private String username;			/* Must be unique, equivalent to uid in frontend */
	private String firstName;
	private String lastName;
	/* private Location location;*/

	private boolean isModerator;
	private boolean isPublic;
	private double reputationReq;		/* Represents fraction of user rep */
	private int proximityReq;

	private int reputation;
	private ArrayList<ObjectId> skillIDs;

	private ArrayList<ObjectId> groupIDs;
	private ArrayList<ObjectId> friendIDs;
	private ArrayList<ObjectId> messageIDs;
	
	public Account(String username, String firstName, String lastName)
	{
		this.accountID = new ObjectId();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = false;
		this.isPublic = false;
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.reputation = 0;
		this.skillIDs = new ArrayList<>();
		this.groupIDs = new ArrayList<>();
		this.friendIDs = new ArrayList<>();
		this.messageIDs = new ArrayList<>();
	}

	public void addSkill(ObjectId skillID)
	{
		this.skillIDs.add(skillID);
	}

	public void removeSkill(ObjectId skillID)
	{
		skillIDs.remove(skillIDs.indexOf(skillID));
	}

	public void sendMessage(ObjectId messageID)
	{
		this.messageIDs.add(messageID);
	}

	/*public Message makeFriendInvite(String content)
	{
		return new Message(content, this.accountID, "Friend Invite");
	}
	*/

}
