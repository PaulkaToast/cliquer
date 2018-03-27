
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

	private Map<String, String> skillReqs;
    private boolean isPublic;
    private double reputationReq;			/* Fraction of leader's reputation */
    private int proximityReq;

    private String groupLeaderID;
    private String groupLeaderName;

    // TODO: map to names
	private Map<String, String> groupMemberIDs;	/* Account ID of the group members */

	private String kickCandidate;
	private List<String> kickVotes;

	private Map<String, List<String>> ratingsToGive;		/* Members that each group member can rate */
	private int maxRatings;

	@JsonIgnore
	private List<ChatMessage> chatHistory;

	public Group(@NonNull String groupName, String groupPurpose, Account groupLeader) {
		this.groupID = new ObjectId().toString();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeader.getAccountID();
		this.groupLeaderName = groupLeader.getFullName();

		this.groupPic = null;
		this.skillReqs = new TreeMap<>();
		this.isPublic = false;
		this.reputationReq = 0.0;
		this.proximityReq = 100;
		this.groupMemberIDs = new TreeMap<>();
		this.groupMemberIDs.put(groupLeader.getAccountID(), groupLeader.getFullName());

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

	public void addSkillReq(Skill skill)
	{
		skillReqs.put(skill.getSkillID(), skill.getSkillName());
	}

	public void removeSkillReq(String skillID)
	{
		skillReqs.remove(skillID);
	}

	public void addGroupMember(Account account)
	{
		groupMemberIDs.put(account.getAccountID(), account.getFullName());
	}

	public void removeGroupMember(String accountID)
	{
		groupMemberIDs.remove(accountID);
	}

	public boolean hasGroupMember(String accountID)
	{
		return groupMemberIDs.containsKey(accountID);
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
		for(String accountID : groupMemberIDs.keySet())
		{
			List<String> members = new ArrayList<>();
			for(String memberID : groupMemberIDs.keySet())
			{
				if(!memberID.equals(accountID))
				{
					members.add(memberID);
				}
			}
			ratingsToGive.put(accountID, members);
		}
		maxRatings--;
		return true;
	}

	public boolean canGiveRating(String raterID, String rateeID)
	{
		List<String> toGive = ratingsToGive.get(raterID);
		if (toGive != null) {
            return ratingsToGive.get(raterID).remove(rateeID);
        }
        return false;
	}

}

