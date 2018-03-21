
package com.styxxco.cliquer.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
public class Group extends Searchable {
	@Id
	private final ObjectId groupID;
	private final String gid;

    private String groupName;
    private String groupPurpose;
    private byte[] groupPic;
	
	private ArrayList<ObjectId> skillReqs;
    private boolean isPublic;
    private double reputationReq;			/* Fraction of leader's reputation */
    private int proximityReq;

    private ObjectId groupLeaderID;
	private List<ObjectId> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	private ObjectId kickCandidate;
	private List<ObjectId> kickVotes;

	public Group(@NonNull String groupName, String groupPurpose, ObjectId groupLeaderID) {
		this.groupID = new ObjectId();
		this.gid = this.groupID.toString();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;

		this.groupPic = null;
		this.skillReqs = new ArrayList<>();
		this.isPublic = false;
		this.reputationReq = 0.0;
		this.proximityReq = 100;
		this.groupMemberIDs = new ArrayList<>();
		this.groupMemberIDs.add(groupLeaderID);

		this.kickCandidate = null;
		this.kickVotes = new ArrayList<>();
	}

	public void addKickVote(ObjectId accountID)
    {
        kickVotes.add(accountID);
    }

    public void removeKickVote(ObjectId accountID)
    {
        kickVotes.remove(accountID);
    }

    public boolean hasKickVote(ObjectId accountID)
    {
        return kickVotes.contains(accountID);
    }

	public void addSkillReq(ObjectId skillID)
	{
		skillReqs.add(skillID);
	}

	public void removeSkillReq(ObjectId skillID)
	{
		skillReqs.remove(skillID);
	}

	public void addGroupMember(ObjectId accountID)
	{
		groupMemberIDs.add(accountID);
	}

	public void removeGroupMember(ObjectId accountID)
	{
		groupMemberIDs.remove(accountID);
	}

	public boolean hasGroupMember(ObjectId accountID)
	{
		return groupMemberIDs.contains(accountID);
	}

	/*
	public Message makeAccountInvite(String content)
	{
		return new Message(content, this.groupID, "Group Invite");
	}
	*/	
}

