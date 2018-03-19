
package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
@ToString(of = {"username", "firstName", "lastName", "reputation"})
public class Account extends Searchable implements UserDetails {

	private static final long serialVersionUID = 4815877135015943617L;

	@Id
	@JsonIgnore
	private final ObjectId accountID;

    private String username;			/* Must be unique, equivalent to uid in frontend */
	private String email;
	private String firstName;
	private String lastName;

	@JsonIgnore
	private String password;

	private boolean isModerator;
	private boolean isNewUser;
	@JsonIgnore
	private int loggedInTime;			/* Minutes that user has spent logged in */
	@JsonIgnore
	private LocalTime intervalTimer;

	/* Start changeable settings */
	private boolean isPublic;
	private boolean isOptedOut;
	private double reputationReq;		/* Represents fraction of user rep */
	private int proximityReq;

	/* Inherited from UserDetails */
	@JsonIgnore
	private boolean accountLocked;
	@JsonIgnore
	private boolean accountExpired;
	@JsonIgnore
	private boolean accountEnabled;
	@JsonIgnore
	private boolean credentialsExpired;

	public static final int MAX_REP = 100;
	public static final int MAX_SKILL = 10;
	public static final int NEW_USER_HOURS = 24;
	public static final int NEW_USER_REP = 50;		/* Reputation constant added to new user reputation */

	private int reputation;
	@JsonIgnore
    private List<Role> authorities;
	@JsonIgnore
    private List<ObjectId> skillIDs;
	@JsonIgnore
    private List<ObjectId> groupIDs;
	@JsonIgnore
    private List<ObjectId> friendIDs;
	@JsonIgnore
    private List<ObjectId> messageIDs;

    public Account() {
    	this.accountID = new ObjectId();
	}

	public Account(@NonNull String username, String email, String firstName, String lastName)	{
		this.accountID = new ObjectId();
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = false;
		this.isPublic = false;
		this.isOptedOut = false;
		this.isNewUser = true;
		this.reputationReq = 0;
		this.proximityReq = 10;
		this.loggedInTime = 0;
		this.intervalTimer = LocalTime.now();
		this.reputation = 1;
		this.skillIDs = new ArrayList<>();
		this.groupIDs = new ArrayList<>();
		this.friendIDs = new ArrayList<>();
		this.messageIDs = new ArrayList<>();
		this.accountLocked = false;
		this.accountExpired = false;
		this.accountEnabled = true;
		this.credentialsExpired = false;
	}

	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}

	public void setTimer()
	{
		this.intervalTimer = LocalTime.now();
	}

	public void incrementTimer()
	{
		this.loggedInTime += this.intervalTimer.until(LocalTime.now(), MINUTES);
		this.intervalTimer = LocalTime.now();
		if(this.loggedInTime >= NEW_USER_HOURS*60)
		{
			this.isNewUser = false;
		}
	}

	public int getAdjustedReputation()
	{
		if(this.isNewUser)
		{
			return (int)(this.reputation + NEW_USER_REP*(1 - (((double)this.loggedInTime)/(NEW_USER_HOURS*60))));
		}
		return this.reputation;
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

	public boolean hasMessage(ObjectId messageID)
	{
		return this.messageIDs.contains(messageID);
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

	public void addFriend(ObjectId friendID) {
    	this.friendIDs.add(friendID);
	}

	public boolean hasFriend(ObjectId friendID)
	{
		return this.friendIDs.contains(friendID);
	}

	public void removeFriend(ObjectId friendID) {
    	this.friendIDs.remove(friendID);
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Role> authorities) {
		this.authorities = authorities;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return accountEnabled;
	}

	/*public Message makeFriendInvite(String content)
	{
		return new Message(content, this.accountID, "Friend Invite");
	}
	*/

}
