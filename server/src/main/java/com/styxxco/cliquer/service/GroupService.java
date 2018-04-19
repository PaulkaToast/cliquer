package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface GroupService {
    /* Group Creation */
    Group createGroup(String groupName, String groupPurpose, String groupLeaderId);

    /* Group Access */
    Group getUserGroup(String groupId, String accountId);
    Group getPublicGroup(String groupId);

    /* Group Modification */
    Group updateGroupSettings(String groupId, String groupLeaderId, String field, String value);
    Group addGroupMember(String groupId, String groupLeaderId, String accountId);
    Group removeGroupMember(String groupId, String groupLeaderId, String accountId);
    Group deleteGroup(String groupId, String groupLeaderId);

    /* Group Searching */
    /* List<Group> groups is used to chain together filters */
    List<Group> searchBySettings(String username, List<Group> groups);
    List<Group> searchBySkillReqs(List<String> skillRequirements, List<Group> groups);
    List<Group> searchByGroupName(String groupName, List<Group> groups);
    List<Group> searchByLeaderFirstName(String firstName, List<Group> groups);
    List<Group> searchByLeaderLastName(String lastName, List<Group> groups);
    List<Group> searchByLeaderFullName(String firstName, String lastName, List<Group> groups);

    /* Vote kicking services */
    Group startVoteKick(String groupId, String groupLeaderId, String accountId);
    Group closeVoteKick(String groupId, String groupLeaderId);
    Group acceptVoteKick(String groupId, String accountId);
    Group denyVoteKick(String groupId, String accountId);

    /* Joining a Group */
    boolean meetsGroupRequirements(String groupId, String accountId);
    Message requestToJoinGroup(String groupId, String accountId);
    Message acceptJoinRequest(String groupLeaderId, String messageId);
    Message denyJoinRequest(String groupLeaderId, String messageId);

    /* Skill centered services */
    List<Skill> getAllSkillReqs(String groupId);
    Skill getSkillReq(String groupId, String skillName);
    Group addSkillReq(String groupId, String groupLeaderId, String skillName, int skillLevel);
    Group removeSkillReq(String groupId, String groupLeaderId, String skillName);

    /* Skill and reputation rating services */
    void initiateRatings(String groupId, String groupLeaderId);
    Map<String, Integer> getGroupMemberRatingForm(String groupId, String rateeId);
    String rateGroupMember(String groupId, String raterId, String rateeId, boolean endorse, Map<String, Integer> skillRatings);

    /* Event broadcast services */
    List<Account> broadcastEvent(String groupId, String groupLeaderId, String eventName, String description, int proximity, List<String> skillNames);

    /* Group member search services */
    List<Account> inviteEligibleUsers(String groupId, String groupLeaderId);
    Message acceptSearchInvite(String userId, String inviteId);

}