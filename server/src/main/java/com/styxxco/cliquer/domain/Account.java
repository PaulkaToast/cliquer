
package com.styxxco.cliquer.domain;

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
public class Account implements UserDetails {

	private static final long serialVersionUID = 4815877135015943617L;

	@Id
	private final ObjectId accountID;

    private String username;			/* Must be unique, equivalent to uid in frontend */
	private String email;
	private String firstName;
	private String lastName;
	private String password;

	private boolean isModerator;
	private boolean isPublic;
	private boolean isNewUser;
	private boolean isOptedOut;
	private double reputationReq;		/* Represents fraction of user rep */
	private int proximityReq;			/* Miles that matches must fit within */
	private int loggedInTime;			/* Minutes that user has spent logged in */
	private LocalTime intervalTimer;

	/* Inherited from UserDetails */
	private boolean accountLocked;
	private boolean accountExpired;
	private boolean accountEnabled;
	private boolean credentialsExpired;

	public static final int MAX_REP = 100;
	public static final int MAX_SKILL = 10;
	public static final int NEW_USER_HOURS = 24;
	public static final int NEW_USER_REP = 50;		/* Reputation constant added to new user reputation */

	private double latitude;
	private double longitude;
	private int reputation;
    private List<Role> authorities;
    private List<ObjectId> skillIDs;
    private List<ObjectId> groupIDs;
    private List<ObjectId> friendIDs;
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
		this.reputationReq = 0.0;
		this.proximityReq = 50;
		this.loggedInTime = 0;
		this.intervalTimer = LocalTime.now();
		this.reputation = 0;
		this.latitude = 360.00;
		this.longitude = 360.00;
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
	public boolean isAccountNonExpired() {
		return !accountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	@Override
	public boolean isEnabled() {
		return accountEnabled;
	}

	public int distanceTo(double latitude, double longitude)
	{
		if(Math.abs(this.latitude) > 90.00 || Math.abs(this.longitude) > 180.00
				|| Math.abs(latitude) > 90.00 || Math.abs(longitude) > 180.00)
		{
			return Integer.MAX_VALUE;
		}
		double theta = this.longitude - longitude;
		double distance = Math.sin(degToRad(this.latitude)) * Math.sin(degToRad(latitude))
				+ Math.cos(degToRad(this.latitude)) * Math.cos(degToRad(latitude)) * Math.cos(degToRad(this.longitude - longitude));
		distance = Math.acos(distance);
		distance = radToDeg(distance);
		return (int)(distance * 60 * 1.1515);
	}

	public static double degToRad(double degrees)
	{
		return (degrees * Math.PI)/180;
	}

	public static double radToDeg(double radians)
	{
		return (radians * 180)/Math.PI;
	}


}
