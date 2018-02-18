
package com.styxxco.cliquer.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
@ToString(of = {"username", "firstName", "lastName", "reputation"})
public class Account implements UserDetails {

	private static final long serialVersionUID = 4815877135015943617L;

	@Id
	@Generated
	private String accountID;

    private String username;			/* Must be unique, equivalent to uid in frontend */
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	/* private Location location;*/

	private boolean isModerator;
	private boolean isPublic;
	private double reputationReq;		/* Represents fraction of user rep */
	private int proximityReq;

	private int reputation;

	private ArrayList<Skill> skills;
	private List<Role> authorities;
	private ArrayList<String> groupIDs;
	private ArrayList<String> friendIDs;
	private ArrayList<String> messageIDs;

	public Account() {
		//Empty for injection creation
	}

	public Account(String username, String email) {
		this.username = username;
		this.email = email;
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

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	public boolean isAccountNonExpired() {
		return false;
	}

	public boolean isAccountNonLocked() {
		return false;
	}

	public boolean isCredentialsNonExpired() {
		return false;
	}

	public boolean isEnabled() {
		return false;
	}

	/*public Message makeFriendInvite(String content)
	{
		return new Message(content, this.accountID, "Friend Invite");
	}
	*/

}
