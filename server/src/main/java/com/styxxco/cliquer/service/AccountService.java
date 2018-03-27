package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface AccountService extends UserDetailsService {
    /* Account Creation */
    Account registerUser(FirebaseTokenHolder tokenHolder, String firstName, String lastName);
    Account createAccount(String username, String email, String firstName, String lastName);

    /* Account Access */
    Account getProfile(String username, String userid, String type);
    Account getProfile(String userid);
    Account getUserProfile(String username);
    Account getMemberProfile(String username);
    Account maskPublicProfile(Account account);
    Account getPublicProfile(String username);

    /* Account Modification */
    Skill addSkill(String username, String skillName, String skillLevel);
    List<Skill> addSkills(String username, String json);
    Account removeSkill(String username, String skillName);
    Account deleteAccount(String username);
    Account rateUser(String username, String friend, String json);
    Message requestRating(String userId, String groupId);

    /* Account Searching */
    Map<String, ? extends Searchable> searchWithFilter(String type, String query, boolean suggestions, boolean weights);
    Account setAccountSettings(String username, String json);
    Group setGroupSettings(String username, String groupId, String json);
    List<Account> searchByFirstName(String firstName);
    List<Account> searchByLastName(String lastName);
    List<Account> searchByFullName(String firstName, String lastName);
    List<Account> searchByFullName(String fullName);
    List<Account> searchByReputation(int minimumRep, boolean includeSuggested, boolean includeWeights);
    List<Account> searchBySkill(String skillName);
    Account searchByUsername(String username);
    List<Group> searchByGroupName(String groupName);
    List<Group> searchByGroupPublic(String userId);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    List<Skill> getAllValidSkills();
    List<Skill> getAllUserSkills(String userId);
    List<Group> getAllUserGroups(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    List<Message> getNewMessages(String userId);
    Message sendMessage(String username, String receiverID, String content, int type);
    String deleteMessage(String username, String messageID);

    /* Group centered services */
    Group createGroup(String username, String json);
    Group deleteGroup(String username, String groupID);
    Account addToGroup(String username, String groupID);
    Account leaveGroup(String username, String groupID);
    Message inviteToGroup(String username, String friend, String groupID);
    Message acceptGroupInvite(String userId, String inviteId);
    Message rejectGroupInvite(String userId, String inviteId);
    Message requestToGroup(String userId, String leaderId, String groupId);
    Account kickMember(String userId, String kickedId, String groupID);

    /* Friend invite services */
    Message sendFriendInvite(String userId, String receiverID);
    Message acceptFriendInvite(String userId, String inviteID);
    Message rejectFriendInvite(String userId, String inviteID);
    Account addFriend(String userId, String friendID);
    Account removeFriend(String username, String friendID);

    /* Role services */
    List<Role> getAnonRoles();
    List<Role> getUserRoles();
    List<Role> getModRoles();

    /* Miscellaneous logic */
    String checkNewUserFlag(String username);
    List<Account> moveSuggestedToTop(List<Account> accounts, int reputation, boolean includeWeights);
    double getReputationRanking(String username);
    void handleNotifications(String userId, String messageId, boolean accept);
}
