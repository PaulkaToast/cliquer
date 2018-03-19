package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
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
    Group deleteGroup(ObjectId groupID, ObjectId groupLeaderID);

    /* Group Searching */
    /* List<Group> groups is used to chain together filters */
    List<Group> searchBySettings(String username, List<Group> groups);
    List<Group> searchBySkillReqs(List<String> skillRequirements, List<Group> groups);
    List<Group> searchByGroupName(String groupName, List<Group> groups);
    List<Group> searchByLeaderFirstName(String firstName, List<Group> groups);
    List<Group> searchByLeaderLastName(String lastName, List<Group> groups);
    List<Group> searchByLeaderFullName(String firstName, String lastName, List<Group> groups);

    /* Vote kicking services */
    Group startVoteKick(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID);
    Group closeVoteKick(ObjectId groupID, ObjectId groupLeaderID);
    Group acceptVoteKick(ObjectId groupID, ObjectId accountID);
    Group denyVoteKick(ObjectId groupID, ObjectId accountID);

    /* Skill centered services */
    List<Skill> getAllSkillReqs(ObjectId groupID);
    Skill getSkillReq(ObjectId groupID, String skillName);
    Group addSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName, int skillLevel);
    Group removeSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName);

    /* Message centered services */
    Message sendMessage(ObjectId groupID, ObjectId senderID, ObjectId receiverId, String content, int type);
    void sendChatMessage(ChatMessage msg, ObjectId groupID);
}