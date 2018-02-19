package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public interface GroupService {
    /* Group Creation */
    Group createGroup(String groupName, String groupPurpose, ObjectId groupLeaderID);

    /* Group Access */
    Group getUserGroup(ObjectId groupID, ObjectId accountID);
    Group getPublicGroup(ObjectId groupID);

    /* Group Modification */
    Group updateGroupSettings(ObjectId groupID, ObjectId groupLeaderID, String field, String value);
    boolean hasGroupMember(Group group, ObjectId accountID);
    Group addGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID);
    Group removeGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID);

    /* Skill centered services */
    ArrayList<Skill> getAllSkillReqs(ObjectId groupID);
    Skill getSkillReq(ObjectId groupID, String skillName);
    Group addSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName, int skillLevel);
    Group removeSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName);

    /* Message centered services */
    Message sendMessage(ObjectId groupID, ObjectId senderID, ObjectId receiverId, String content, String type);
}