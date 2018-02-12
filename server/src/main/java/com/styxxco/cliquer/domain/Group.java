
package com.styxxco.cliquer.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@ToString(exclude = {"groupID"})

public class Group
{
	@Id
	@Generated
	private String groupID;

	@Setter private String groupName;
	@Setter private String groupPurpose;
	
	private ArrayList<Skill> skillReqs;
	@Setter private int reputationReq;
	@Setter private int proximityReq;

	@Setter private String groupLeaderID;
	private ArrayList<String> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	public Group(@NonNull String groupName, String groupPurpose, String groupLeaderID)
	{
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;

		this.skillReqs = new ArrayList<Skill>();
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.groupMemberIDs = new ArrayList<String>();
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

