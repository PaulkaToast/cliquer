
package com.styxxco.cliquer.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@ToString(exclude = {"groupID"})

public class Group
{
	@Id
	private final ObjectId groupID;

	@Setter
    private String groupName;
	@Setter
    private String groupPurpose;
	
	private ArrayList<Skill> skillReqs;
	@Setter
    private boolean isPublic;
	@Setter
    private int reputationReq;
	@Setter
    private int proximityReq;

	@Setter
    private ObjectId groupLeaderID;
	private ArrayList<ObjectId> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	public Group(@NonNull String groupName, String groupPurpose, ObjectId groupLeaderID)
	{
		this.groupID = new ObjectId();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;

		this.skillReqs = new ArrayList<>();
		this.isPublic = false;
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.groupMemberIDs = new ArrayList<>();
	}

	public void addSkillReq(String skillName, int skillLevel)
	{
		skillReqs.add(new Skill(skillName, skillLevel));
	}

	/*
	public Message makeAccountInvite(String content)
	{
		return new Message(content, this.groupID, "Group Invite");
	}
	*/	
}

