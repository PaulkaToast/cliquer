
package com.styxxco.cliquer.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
@ToString(exclude = {"groupID"})

public class Group
{
	@Id
	private final ObjectId groupID;

    private String groupName;
    private String groupPurpose;
	
	private ArrayList<ObjectId> skillReqs;
    private boolean isPublic;
    private double reputationReq;			/* Fraction of leader's reputation */
    private int proximityReq;

    private ObjectId groupLeaderID;
	private ArrayList<ObjectId> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	public Group(String groupName, String groupPurpose, ObjectId groupLeaderID)
	{
		this.groupID = new ObjectId();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;

		this.skillReqs = new ArrayList<>();
		this.isPublic = false;
		this.reputationReq = 0.0;
		this.proximityReq = 0;
		this.groupMemberIDs = new ArrayList<>();
		this.groupMemberIDs.add(groupLeaderID);
	}

	public void addSkillReq(Skill skill)
	{
		skillReqs.add(skill.getSkillID());
	}

	public void addGroupMember(ObjectId accountID)
	{
		groupMemberIDs.add(accountID);
	}

	public void removeGroupMember(ObjectId accountID)
	{
		groupMemberIDs.remove(accountID);
	}

	/*
	public Message makeAccountInvite(String content)
	{
		return new Message(content, this.groupID, "Group Invite");
	}
	*/	
}

