
package com.styxxco.cliquer.domain;

import lombok.*;
import org.bson.types.ObjectId;
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
	private final ObjectId accountID;

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

    private List<Role> authorities;
    private List<ObjectId> skillIDs;
    private List<ObjectId> groupIDs;
    private List<ObjectId> friendIDs;
    private List<ObjectId> messageIDs;

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
        this.skillIDs = new ArrayList<>();
		this.groupIDs = new ArrayList<>();
		this.friendIDs = new ArrayList<>();
		this.messageIDs = new ArrayList<>();
	}

	public Account(@NonNull String username, String firstName, String lastName)
	{
		this.accountID = new ObjectId();
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = false;
		this.isPublic = false;
		this.reputationReq = 0;
		this.proximityReq = 0;
		this.reputation = 0;
		this.skillIDs = new ArrayList<>();
		this.groupIDs = new ArrayList<>();
		this.friendIDs = new ArrayList<>();
		this.messageIDs = new ArrayList<>();
	}

	public void addSkill(ObjectId skillID)
	{
		this.skillIDs.add(skillID);
	}

	public void removeSkill(ObjectId skillID)
	{
		this.skillIDs.remove(skillID);
	}

	public void addMessage(ObjectId messageID)
	{
		this.messageIDs.add(messageID);
	}

	public void removeMessage(ObjectId messageID)
	{
		this.messageIDs.remove(messageID);
	}

	public void addGroup(ObjectId groupID)
	{
		this.groupIDs.add(groupID);
	}

	public void removeGroup(ObjectId groupID)
	{
		this.groupIDs.remove(groupID);
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
