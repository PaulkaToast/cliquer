package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AccountService extends UserDetailsService {
    /* Account Creation */
    Account registerUser(FirebaseTokenHolder tokenHolder, String firstName, String lastName);
    Account createAccount(String username, String email, String firstName, String lastName);

    /* Account Access */
    Account getProfile(String username, String userId, String type);
    Account getProfile(String userId);
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
    void uploadPicture(String userId, MultipartFile file) throws Exception;
    void requestRating(String userId, String groupId);
    void setLocation(String userId, String latitude, String longitude);

    /* Account Searching */
    Map<String, ? extends Searchable> searchWithFilter(String type, String query);
    Account setAccountSettings(String username, String json);
    Group setGroupSettings(String username, String groupId, String json);
    List<Account> searchByFirstName(String firstName);
    List<Account> searchByLastName(String lastName);
    Map<String, Account> searchByFullName(String firstName, String lastName);
    Map<String, Account> searchByFullName(String fullName);
    List<Account> searchByReputation(int minimumRep, boolean includeSuggested, boolean includeWeights);
    List<Account> searchBySkill(String skillName);
    Account searchByUsername(String username);
    List<Group> searchByGroupName(String groupName);
    List<Group> searchByGroupPublic(String userId);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    Skill addSkillToDatabase(String modId, String skillName);
    List<Skill> getAllValidSkills();
    List<Skill> getAllUserSkills(String userId);
    List<Group> getAllUserGroups(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    /* Note: startDate is of format YEAR-MONTH-DAY, MONTH and DAY are zero padded to reach two digits*/
    List<Message> getMessages(String userId, String read, String startDate);
    Message sendMessage(String senderId, String receiverId, String content, int type);
    Message deleteMessage(String username, String messageId);
    List<Message> getChatHistory(String groupId, String userId);
    Message reactToChatMessage(String groupId, String userId, String messageId, String reaction);
    void handleAcceptNotification(String userId, String messageId);
    void handleRejectNotification(String userId, String messageId);

    /* Group centered services */
    Group createGroup(String username, String json);
    Group deleteGroup(String username, String groupId);
    Account addToGroup(String username, String groupId);
    Account leaveGroup(String username, String groupId);
    Message inviteToGroup(String username, String friend, String groupId);
    Message acceptGroupInvite(String userId, String inviteId);
    Message requestToGroup(String userId, String leaderId, String groupId);
    Message acceptJoinRequest(String userId, String messageId);
    Message rejectJoinRequest(String userId, String messageId);
    Message readMessage(String userId, String messageId);
    Message acceptSearchInvite(String userId, String inviteId);
    Message kickMember(String userId, String kickedId, String groupId);
    Message rejectKickRequest(String userId, String messageId);
    Message acceptKickRequest(String userId, String messageId);
    void startKickVote(String userId, String kickedId, String groupId);
    Map<String, Integer> getRateForm(String userId, String rateeId, String groupId);
    Group createEvent(String groupId, String json);
    List<Account> inviteAll(String userId, String groupId);

    /* Friend invite services */
    Message sendFriendInvite(String userId, String receiverId);
    Message acceptFriendInvite(String userId, String inviteId);
    Message rejectInvite(String userId, String inviteId);
    Account addFriend(String userId, String friendId);
    Account removeFriend(String username, String friendId);

    /* Moderator related services */
    Message sendMessageToMods(String senderId, Message message);
    Message acceptModInvite(String userId, String messageId);
    Message acceptModRequest(String userId, String messageId);
    Message rejectModInvite(String userId, String messageId);
    Message rejectModRequest(String userId, String messageId);
    Message reportGroupMember(String groupId, String reporterId, String messageId, String reason);
    List<Message> getReportContext(String modId, String messageId, String startId, String endId);
    List<Message> getMessageHistory(String modId, String userId);
    Message checkModStatus (String userId);
    Account addToModerators (String userId);
    Account flagUser(String modId, String messageId);
    Account suspendUser(String modId, String messageId);
    Message reportUser(String userId, String reporteeId, String reason);
    List<String> getActivityLog(String modId, String userId, String startDate, String endDate);

    /* Role services */
    List<Role> getAnonRoles();
    List<Role> getUserRoles();
    List<Role> getModRoles();

    /* Miscellaneous logic */
    String checkNewUserFlag(String username);
    List<Account> moveSuggestedToTop(List<Account> accounts, int reputation, boolean includeWeights);
    double getReputationRanking(String username);
    void deleteMessageByParent(String parentId);
    void handleNotifications(String userId, String messageId, boolean accept);

    /* Bypass services */
    Account editUserProfile(String modId, String userId, String field, String value);
}
