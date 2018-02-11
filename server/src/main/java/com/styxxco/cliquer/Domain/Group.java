
package com.styxxco.cliquer;

import java.util.*;
import org.springframework.data.annotation.Id;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@lombok.Getter
@lombok.ToString(exclude = {"messageID", "type"})

public class Group
{
	//@Id private final String groupID;

	@lombok.Setter private String groupName;
	@lombok.Setter private String groupPurpose;
	
	private ArrayList<Skill> skillReqs;
	@lombok.Setter private int reputationReq;
	@lombok.Setter private int proximityReq;

	@lombok.Setter private String groupLeaderID;
	private ArrayList<String> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	public Group(@lombok.NonNull String groupName, String groupPurpose, String groupLeaderID)
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

