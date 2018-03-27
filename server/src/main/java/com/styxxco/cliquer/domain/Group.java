
package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.styxxco.cliquer.database.ObjectIdSerial;
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
	private final String groupID;

    private String groupName;
    private String groupPurpose;
    private byte[] groupPic;

	private List<String> skillReqs;
    private boolean isPublic;
    private double reputationReq;			/* Fraction of leader's reputation */
    private int proximityReq;

    private String groupLeaderID;
  
    private String ownerUID;

    // TODO: map to names
	private List<String> groupMemberIDs;	/* Account ID of the group members */

	private String kickCandidate;
	private List<String> kickVotes;

	private Map<String, List<String>> ratingsToGive;		/* Members that each group member can rate */
	private int maxRatings;

	@JsonIgnore
	private List<ChatMessage> chatHistory;

	public Group(@NonNull String groupName, String groupPurpose, String groupLeaderID) {
		this.groupID = new ObjectId().toString();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;
		this.ownerUID = groupLeaderID.toString();

		this.groupPic = null;
		this.skillReqs = new ArrayList<>();
		this.isPublic = false;
		this.reputationReq = 0.0;
		this.proximityReq = 100;
		this.groupMemberIDs = new ArrayList<>();
		this.groupMemberIDs.add(groupLeaderID);

		this.kickCandidate = null;
		this.kickVotes = new ArrayList<>();
		this.ratingsToGive = new TreeMap<>();
		this.maxRatings = 1;

		this.chatHistory = new ArrayList<>();
	}

	public void addKickVote(String accountID)
    {
        kickVotes.add(accountID);
    }

    public void removeKickVote(String accountID)
    {
        kickVotes.remove(accountID);
    }

    public boolean hasKickVote(String accountID)
    {
        return kickVotes.contains(accountID);
    }

	public void addSkillReq(String skillID)
	{
		skillReqs.add(skillID);
	}

	public void removeSkillReq(String skillID)
	{
		skillReqs.remove(skillID);
	}

	public void addGroupMember(String accountID)
	{
		groupMemberIDs.add(accountID);
	}

	public void removeGroupMember(String accountID)
	{
		groupMemberIDs.remove(accountID);
	}

	public boolean hasGroupMember(String accountID)
	{
		return groupMemberIDs.contains(accountID);
	}

	/*
	public Message makeAccountInvite(String content)
	{
		return new Message(content, this.groupID, "Group Invite");
	}
	*/
	public void addMessage(ChatMessage msg) { 
    chatHistory.add(msg);
	}

	public boolean startMemberRatings()
	{
		if(maxRatings <= 0)
		{
			return false;
		}
		ratingsToGive = new TreeMap<>();
		for(String accountID : groupMemberIDs)
		{
			List<String> members = new ArrayList<>();
			Collections.copy(members, groupMemberIDs);
			members.remove(accountID);
			ratingsToGive.put(accountID, members);
		}
		maxRatings--;
		return true;
	}

	public boolean canGiveRating(String raterID, String rateeID)
	{
		return ratingsToGive.get(raterID).remove(rateeID);
	}

}

