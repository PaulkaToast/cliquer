
package com.styxxco.cliquer.Domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@ToString(exclude = {"messageID", "type"})

public class Group
{
	@Id private final String groupID;

	@Setter private String groupName;
	@Setter private String groupPurpose;
	
	private List<Skill> skillReqs;
	@Setter private int reputationReq;
	@Setter private int proximityReq;

	@Setter private String groupLeaderID;
	private List<String> groupMemberIDs;	/* Account ID of the group members */
	/* private ChatLog chat */

	public Group(@NonNull String groupName, String groupPurpose, String groupLeaderID)
	{
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;

		this.skillReqs = new ArrayList<String>();
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.groupMemberIDs = new ArrayList<String>();
	}

	public void addSkillReq(String skillName, int skillLevel)
	{
		skillReqs.add(new Skill(skillName, skillLevel);
	}

	public Message makeAccountInvite(String content)
	{
		return new Message(content, this.groupID, "Group Invite");
	}
		
}

