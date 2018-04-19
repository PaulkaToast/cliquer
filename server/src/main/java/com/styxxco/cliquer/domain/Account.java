
package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.styxxco.cliquer.service.AccountService;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private final String accountID;

    private String username;			/* Must be unique, equivalent to uid in frontend */
	private String email;
	private String firstName;
	private String lastName;

	@JsonIgnore
	private String password;

	private String picturePath;

	private boolean isModerator;
	private boolean deniedMod;
	private boolean isNewUser;
	private boolean canSuspend;
	@JsonIgnore
	private int loggedInTime;			/* Minutes that user has spent logged in */
	@JsonIgnore
	private LocalTime intervalTimer;

	/* Start changeable settings */
	private boolean isPublic;
	private boolean isOptedOut;
	private double reputationReq;		/* Represents fraction of user rep */
	private int proximityReq;			/* Miles that matches must fit within */

	/* Inherited from UserDetails */
	@JsonIgnore
	private boolean accountLocked;
	@JsonIgnore
	private boolean accountExpired;
	@JsonIgnore
	private boolean accountEnabled;
	@JsonIgnore
	private boolean credentialsExpired;
	@JsonIgnore
	public long suspendTime; 			/* Amount of time in minutes the user is suspended */
	@JsonIgnore
	public LocalDateTime startSuspendTime;		/* Local Time the user started the suspend */

	public static final int MAX_REP = 100;
	public static final int MAX_SKILL = 10;
	public static final int NEW_USER_HOURS = 24;
	public static final int NEW_USER_REP = 50;		/* Reputation constant added to new user reputation */

	private double latitude;
	private double longitude;
	private int reputation;
	private double rank;
	@JsonIgnore
	private int flags;
	@JsonIgnore
	private List<String> logs;

	@JsonIgnore
    private List<Role> authorities;
	@JsonIgnore
    private Map<String, String> skillIDs;
	@JsonIgnore
    private Map<String, String> groupIDs;

    private Map<String, String> friendIDs;
	@JsonIgnore
    private Map<String, Integer> messageIDs;

	@JsonIgnore
	private Map<String, Integer> numRatings;		/* Mapping for number of times each skill has been rated */
	@JsonIgnore
	private Map<String, Integer> totalRating;		/* Mapping for cumulative value of ratings for each skill */
	@JsonIgnore
	private Map<String, Boolean> flaggedUser; 		/* Mapping for whether they flagged a certain user */

    public Account() {
    	this.accountID = new ObjectId().toString();
	}

	public Account(@NonNull String username, String email, String firstName, String lastName)	{
		this.accountID = new ObjectId().toString();
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isModerator = false;
		this.isPublic = false;
		this.isOptedOut = false;
		this.isNewUser = true;
		this.reputationReq = 0;
		this.proximityReq = 50;
		this.loggedInTime = 0;
		this.intervalTimer = LocalTime.now();
		this.reputation = 1;
		this.rank = 0;
		this.latitude = 0.0;//360.00;
		this.longitude = 0.0;//360.00;
		this.skillIDs = new TreeMap<>();
		this.groupIDs = new TreeMap<>();
		this.friendIDs = new TreeMap<>();
		this.messageIDs = new TreeMap<>();
		this.accountLocked = false;
		this.accountExpired = false;
		this.accountEnabled = true;
		this.credentialsExpired = false;
		this.numRatings = new TreeMap<>();
		this.totalRating = new TreeMap<>();
		this.deniedMod = false;
		this.logs = new ArrayList<>();
		this.flaggedUser = new TreeMap<>();
		this.canSuspend = false;
		this.picturePath = null;

		if(email.equals("buckmast@purdue.edu") || email.equals("knagar@purdue.edu") || email.equals("montgo38@purdue.edu")
				|| email.equals("reed226@purdue.edu") || email.equals("toth21@purdue.edu")){
			this.isModerator = true;
		}
	}

	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}

	public void setTimer()
	{
		this.intervalTimer = LocalTime.now();
	}

	public void incrementTimer() {
		this.loggedInTime += this.intervalTimer.until(LocalTime.now(), MINUTES);
		this.intervalTimer = LocalTime.now();
		if(this.loggedInTime >= NEW_USER_HOURS*60)
		{
			this.isNewUser = false;
		}
	}

	public int getAdjustedReputation() {
		if(this.isNewUser) {
			return (int)(this.reputation + NEW_USER_REP*(1 - (((double)this.loggedInTime)/(NEW_USER_HOURS*60))));
		}
		return this.reputation;
	}

	public void addSkill(Skill skill)
	{
		this.skillIDs.put(skill.getSkillID(), skill.getSkillName());
	}

	public void removeSkill(String skillID)
	{
		this.skillIDs.remove(skillID);
	}

	public void addMessage(Message message)
	{
		this.messageIDs.put(message.getMessageID(), message.getType());
	}

	public boolean hasMessage(String messageID)
	{
		return this.messageIDs.containsKey(messageID);
	}

	public void removeMessage(String messageID)
	{
		this.messageIDs.remove(messageID);
	}

	public void addGroup(Group group)
	{
		this.groupIDs.put(group.getGroupID(), group.getGroupName());
	}

	public void removeGroup(String groupID)
	{
		this.groupIDs.remove(groupID);
	}

	public void addFriend(Account friend) {
    	this.friendIDs.put(friend.getAccountID(), friend.getFullName());
	}

	public boolean hasFriend(String friendID)
	{
		return this.friendIDs.containsKey(friendID);
	}

	public void removeFriend(String friendID) {
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

	public int distanceTo(double latitude, double longitude) {
		if(Math.abs(this.latitude) > 90.00 || Math.abs(this.longitude) > 180.00
				|| Math.abs(latitude) > 90.00 || Math.abs(longitude) > 180.00) {
			return Integer.MAX_VALUE;
		}
		double distance = Math.sin(degToRad(this.latitude)) * Math.sin(degToRad(latitude))
				+ Math.cos(degToRad(this.latitude)) * Math.cos(degToRad(latitude)) * Math.cos(degToRad(this.longitude - longitude));
		distance = Math.acos(distance);
		distance = radToDeg(distance);
		return (int)(distance * 60 * 1.1515);
	}

	private static double degToRad(double degrees)
	{
		return (degrees * Math.PI)/180;
	}

	private static double radToDeg(double radians)
	{
		return (radians * 180)/Math.PI;
	}

	public Map<String, Integer> addSkillRatings(List<Skill> skills, List<Integer> ratings) {
		Map<String, Integer> adjustedSkills = new TreeMap<>();
		for(int i = 0; i < skills.size(); i++) {
			String skillName = skills.get(i).getSkillName();
			int rating = ratings.get(i);
			if(numRatings.containsKey(skillName)) {
				numRatings.replace(skillName, numRatings.get(skillName) + 1);
				totalRating.replace(skillName, totalRating.get(skillName) + rating);
			}
			else {
				numRatings.put(skillName, 1);
				totalRating.put(skillName, rating);
			}
			if(Math.abs(totalRating.get(skillName) / numRatings.get(skillName) - skills.get(i).getSkillLevel()) >= 1) {
				skillIDs.remove(skills.get(i).getSkillID());
				adjustedSkills.put(skillName, totalRating.get(skillName) / numRatings.get(skillName));
			}
		}
		return adjustedSkills;
	}

	public void deniedMod() {
		this.deniedMod = true;
	}

	public void addFlag() {
		this.flags++;
		if (this.flags > 2) {
			this.canSuspend = true;
		}
	}

	public void removeFlag() {
		this.flags--;
	}

	/* Returns time still needed served and unsuspends if time is less than 0 */
	public long tryUnsuspend() {
    	long served = this.startSuspendTime.until(LocalDateTime.now(), MINUTES);
    	long timeLeft = suspendTime - served;
    	if (timeLeft < 0) {
    		startSuspendTime = null;
    		accountEnabled = true;
		}
		return timeLeft;
	}

	public void suspend(long minutes) {
		this.accountEnabled = false;
		this.startSuspendTime = LocalDateTime.now();
		this.suspendTime = minutes;
	}

	public void log(String log) {
		this.logs.add(log + " at " + LocalTime.now() + " on " + LocalDate.now());
	}

	public boolean hasFlagged(String userId) {
    	if (!this.flaggedUser.containsKey(userId)) {
			this.flaggedUser.put(userId, false);
		}
		return this.flaggedUser.get(userId);
	}

	public void toggleFlag(String userId) {
		boolean curr = this.flaggedUser.get(userId);
    	this.flaggedUser.put(userId, !curr);
	}

	public boolean canSuspend() {
    	return canSuspend;
	}
}
