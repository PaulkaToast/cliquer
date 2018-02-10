
package com.styxxco.cliquer.Domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@ToString(of = {"username", "firstName", "lastName", "reputation"})

public class Account
{
	@Id private final String accountID;

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
	private List<Skill> skills;
	
	private List<String> groupIDs;
	private List<String> friendIDs;
	private List<String> messageIDs;
	
	public Account(@NonNull String username, String firstName, String lastName)
	{
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = 0;
		this.isPublic = 0;
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
			if(s.skillName == skillName)
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
			if(s.skillName == skillName)
			{
				skills.remove(indexOf(s));
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
		Skill candidate = getSkill(skill.skillName);
		if(candidate && candidate.skillLevel >= skill.skillLevel)
		{
			return true;
		}
		return false;
	}

	public Message makeFriendInvite(String content)
	{
		return new Message(content, this.accountID, "Friend Invite");
	}


}
