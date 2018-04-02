package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface GroupService {
    /* Group Creation */
    Group createGroup(String groupName, String groupPurpose, String groupLeaderID);

    /* Group Access */
    Group getUserGroup(String groupID, String accountID);
    Group getPublicGroup(String groupID);

    /* Group Modification */
    Group updateGroupSettings(String groupID, String groupLeaderID, String field, String value);
    Group addGroupMember(String groupID, String groupLeaderID, String accountID);
    Group removeGroupMember(String groupID, String groupLeaderID, String accountID);
    Group deleteGroup(String groupID, String groupLeaderID);

    /* Group Searching */
    /* List<Group> groups is used to chain together filters */
    List<Group> searchBySettings(String username, List<Group> groups);
    List<Group> searchBySkillReqs(List<String> skillRequirements, List<Group> groups);
    List<Group> searchByGroupName(String groupName, List<Group> groups);
    List<Group> searchByLeaderFirstName(String firstName, List<Group> groups);
    List<Group> searchByLeaderLastName(String lastName, List<Group> groups);
    List<Group> searchByLeaderFullName(String firstName, String lastName, List<Group> groups);

    /* Vote kicking services */
    Group startVoteKick(String groupID, String groupLeaderID, String accountID);
    Group closeVoteKick(String groupID, String groupLeaderID);
    Group acceptVoteKick(String groupID, String accountID);
    Group denyVoteKick(String groupID, String accountID);

    /* Joining a Group */
    boolean meetsGroupRequirements(String groupID, String accountID);
    Message requestToJoinGroup(String groupID, String accountID);
    Message acceptJoinRequest(String groupLeaderID, String messageID);
    Message denyJoinRequest(String groupLeaderID, String messageID);

    /* Skill centered services */
    List<Skill> getAllSkillReqs(String groupID);
    Skill getSkillReq(String groupID, String skillName);
    Group addSkillReq(String groupID, String groupLeaderID, String skillName, int skillLevel);
    Group removeSkillReq(String groupID, String groupLeaderID, String skillName);

    /* Message centered services */
    Message sendMessage(String groupID, String senderID, String receiverId, String content, int type);
    void sendChatMessage(ChatMessage msg, String groupID);

    /* Skill and reputation rating services */
    Message initiateRatings(String groupID, String groupLeaderID);
    Map<String, Integer> getGroupMemberRatingForm(String groupID, String rateeID);
    String rateGroupMember(String groupID, String raterID, String rateeID, boolean endorse, Map<String, Integer> skillRatings);

    /* Event broadcast services */
    List<Account> broadcastEvent(String groupID, String groupLeaderID, String description, int proximity, List<String> skillNames);

    /* Group member search services */
}