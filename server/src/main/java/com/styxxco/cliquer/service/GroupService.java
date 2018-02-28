package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

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
    String deleteGroup(ObjectId groupID, ObjectId groupLeaderID);

    /* Group Searching */
    /* List<Group> groups is used to chain together filters */
    List<Group> searchBySettings(String username, List<Group> groups);
    List<Group> searchBySkillReqs(String username, List<String> skillRequirements, List<Group> groups);
    List<Group> searchByGroupName(String username, String groupName, List<Group> groups);
    List<Group> searchByLeaderFirstName(String username, String firstName, List<Group> groups);
    List<Group> searchByLeaderLastName(String username, String lastName, List<Group> groups);
    List<Group> searchByLeaderFullName(String username, String firstName, String lastName, List<Group> groups);

    /* Skill centered services */
    List<Skill> getAllSkillReqs(ObjectId groupID);
    Skill getSkillReq(ObjectId groupID, String skillName);
    Group addSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName, int skillLevel);
    Group removeSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName);

    /* Message centered services */
    Message sendMessage(ObjectId groupID, ObjectId senderID, ObjectId receiverId, String content, int type);
}