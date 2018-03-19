package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface AccountService extends UserDetailsService {
    /* Account Creation */
    Account registerUser(FirebaseTokenHolder tokenHolder, String firstName, String lastName);
    Account createAccount(String username, String email, String firstName, String lastName);

    /* Account Access */
    Account getProfile(String username, String type);
    Account getUserProfile(String username);
    Account getMemberProfile(String username);
    Account maskPublicProfile(Account account);
    Account getPublicProfile(String username);

    /* Account Modification */
    Skill addSkill(String username, String skillName, String skillLevel);
    List<Skill> addSkills(String username, String json);
    Account addSkill(String username, String skillName, String skillLevel);
    Account addSkills(String username, String json);
    Account removeSkill(String username, String skillName);
    Account deleteAccount(String username);
    Account rateUser(String username, String friend, String json);

    /* Account Searching */
    Map<String, ? extends Searchable> searchWithFilter(String type, String query, boolean suggestions, boolean weights);
    Account setAccountSettings(String username, String json);
    Group setGroupSettings(String username, ObjectId groupId, String json);
    List<Account> searchByFirstName(String firstName);
    List<Account> searchByLastName(String lastName);
    List<Account> searchByFullName(String firstName, String lastName);
    List<Account> searchByFullName(String fullName);
    List<Account> searchByReputation(int minimumRep, boolean includeSuggested, boolean includeWeights);
    List<Account> searchBySkill(String skillName);
    Account searchByUsername(String username);
    List<Group> searchByGroupName(String groupName);
    List<Group> searchByGroupPublic(boolean isPublic);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    List<Skill> getAllValidSkills();
    List<Skill> getAllUserSkills(String username);
    List<Group> getAllUserGroups(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    List<Message> getNewMessages(String username);
    List<Message> getGroupChatLog(String username, String groupId, int lower, int upper);
    Message sendMessage(String username, ObjectId receiverID, String content, int type);

    /* Group centered services */
    Group createGroup(String username, String json);
    Group deleteGroup(String username, ObjectId groupID);
    Account addToGroup(String username, ObjectId groupID);
    Account leaveGroup(String username, ObjectId groupID);
    Account inviteToGroup(String username, String friend, ObjectId groupID);
    Account kickMember(String username, String friend, ObjectId groupID);

    /* Friend invite services */
    Message sendFriendInvite(String username, ObjectId receiverID);
    Account acceptFriendInvite(String username, ObjectId inviteID);
    String rejectFriendInvite(String username, ObjectId inviteID);
    Account addFriend(String username, ObjectId friendID);
    Account removeFriend(String username, ObjectId friendID);

    /* Role services */
    List<Role> getAnonRoles();
    List<Role> getUserRoles();
    List<Role> getModRoles();

    /* Miscellaneous logic */
    String checkNewUserFlag(String username);
    List<Account> moveSuggestedToTop(List<Account> accounts, int reputation, boolean includeWeights);
    double getReputationRanking(String username);
}
