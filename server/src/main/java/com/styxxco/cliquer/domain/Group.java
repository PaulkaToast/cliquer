
package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@JsonIgnore
	private final ObjectId groupID;
	private final String gid;

    private String groupName;
    private String groupPurpose;
    private byte[] groupPic;

    @JsonIgnore
	private List<ObjectId> skillReqs;
    private boolean isPublic;
    private double reputationReq;			/* Fraction of leader's reputation */
    private int proximityReq;

    @JsonIgnore
    private ObjectId groupLeaderID;
    private String ownerUID;

    @JsonIgnore
	private List<ObjectId> groupMemberIDs;	/* Account ID of the group members */

	@JsonIgnore
	private List<ObjectId> chatLog;

	public Group(@NonNull String groupName, String groupPurpose, ObjectId groupLeaderID) {
		this.groupID = new ObjectId();
		this.gid = this.groupID.toString();
		this.groupName = groupName;
		this.groupPurpose = groupPurpose;
		this.groupLeaderID = groupLeaderID;
		this.ownerUID = groupLeaderID.toString();

		this.groupPic = null;
		this.skillReqs = new ArrayList<>();
		this.isPublic = false;
		this.reputationReq = 0;
		this.proximityReq = 10;
		this.groupMemberIDs = new ArrayList<>();
		this.groupMemberIDs.add(groupLeaderID);
	}

	public void addSkillReq(ObjectId skillID)
	{
		skillReqs.add(skillID);
	}

	public void removeSkillReq(ObjectId skillID)
	{
		skillReqs.remove(skillID);
	}

	public void addGroupMember(ObjectId accountID)
	{
		groupMemberIDs.add(accountID);
	}

	public void removeGroupMember(ObjectId accountID)
	{
		groupMemberIDs.remove(accountID);
	}

	public List<ObjectId> getChatLogsFrom(int from, int to) {
		return chatLog.subList(from, to);
	}

	public void addMessage(Message message) {
		chatLog.add(message.getMessageID());
	}
}

