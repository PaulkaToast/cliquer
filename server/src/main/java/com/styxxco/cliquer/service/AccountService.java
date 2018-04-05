package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    Account rateUser(String userId, String rateeId, String messageId, String json, boolean endorse);
    void requestRating(String userId, String groupId);

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
    /* Note: startDate is of format YEAR-MONTH-DAY, MONTH and DAY are zero padded to reach two digits*/
    List<Message> getMessages(String userId, boolean includeRead, String startDate);
    Message sendMessage(String username, String receiverID, String content, int type);
    Message sendMessageToMods(String senderId, Message message);
    Message deleteMessage(String username, String messageID);
    ChatMessage sendChatMessageFromGroup(String groupId, ChatMessage message);
    ChatMessage sendChatMessageFromUser(String groupId, ChatMessage message);
    List<ChatMessage> getChatHistory(String groupId, String username);
    void handleAcceptNotification(String userId, String messageId);
    void handleRejectNotification(String userId, String messageId);

    /* Group centered services */
    Group createGroup(String username, String json);
    Group deleteGroup(String username, String groupID);
    Account addToGroup(String username, String groupID);
    Account leaveGroup(String username, String groupID);
    Message inviteToGroup(String username, String friend, String groupID);
    Message acceptGroupInvite(String userId, String inviteId);
    Message requestToGroup(String userId, String leaderId, String groupId);
    Message acceptJoinRequest(String userId, String messageId);
    Message rejectJoinRequest(String userId, String messageId);
    Message acceptModInvite(String userId, String messageId);
    Message acceptModRequest(String userId, String messageId);
    Message rejectModInvite(String userId, String messageId);
    Message rejectModRequest(String userId, String messageId);
    Message readMessage(String userId, String messageId);
    Message acceptSearchInvite(String userId, String inviteId);
    Message kickMember(String userId, String kickedId, String groupID);
    Map<String, Integer> getRateForm(String userId, String rateeId, String groupId);
    Group createEvent(String groupId, String json);
    List<Account> inviteAll(String userId, String groupId);

    /* Friend invite services */
    Message sendFriendInvite(String userId, String receiverID);
    Message acceptFriendInvite(String userId, String inviteID);
    Message rejectInvite(String userId, String inviteID);
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
    void deleteMessageByParent(String parentId);
    void checkModStatus (String userId);
    void addToModerators (String userId);
    int flagUser(String modId, String userId);
    void handleNotifications(String userId, String messageId, boolean accept);
}
