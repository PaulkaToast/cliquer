
package com.styxxco.cliquer.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@ToString(of = {"username", "firstName", "lastName", "reputation"})

public class Account
{
	@Id
	@Generated
	private String accountID;

	@Setter private boolean isModerator;
    @Setter private String username;			/* Must be unique					*/
    @Setter private String firstName;
	@Setter private String lastName;
	/*@Setter private Location location;*/
	
	@Setter private boolean isPublic;
	@Setter private boolean isFacebookLinked;
	@Setter private double reputationReq;		/* Represents fraction of user rep 	*/
	@Setter private int proximityReq;
	
	@Setter private int reputation;			
	private ArrayList<Skill> skills;
	
	private ArrayList<String> groupIDs;
	private ArrayList<String> friendIDs;
	private ArrayList<String> messageIDs;
	
	public Account(@NonNull String username, String firstName, String lastName)
	{
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = false;
		this.isPublic = false;
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.reputation = 0;
		this.skills = new ArrayList<Skill>();
		this.groupIDs = new ArrayList<String>();
		this.friendIDs = new ArrayList<String>();
		this.messageIDs = new ArrayList<String>();
	}

	public Skill getSkill(String skillName)
	{
		for(Skill s : this.skills)
		{
			if(s.getSkillName() == skillName)
			{
				return s;
			}
		}
		return null;
	}

	public void addSkill(@NonNull Skill skill)
	{
		this.skills.add(skill);
	}

	public boolean removeSkill(String skillName)
	{
		for(Skill s : this.skills)
		{
			if(s.getSkillName() == skillName)
			{
				skills.remove(skills.indexOf(s));
				return true;
			}
		}
		return false;
	}

	public void sendMessage(String messageID)
	{
		this.messageIDs.add(messageID);
	}

	public boolean meetSkillReq(Skill skill)
	{
		Skill candidate = getSkill(skill.getSkillName());
		if(candidate != null && candidate.getSkillLevel() >= skill.getSkillLevel())
		{
			return true;
		}
		return false;
	}

	/*public Message makeFriendInvite(String content)
	{
		return new Message(content, this.accountID, "Friend Invite");
	}
	*/

}
