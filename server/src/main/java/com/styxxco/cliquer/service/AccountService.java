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
    Account getUserProfile(String username);
    Account getMemberProfile(ObjectId accountID);
    Account maskPublicProfile(Account account);
    Account getPublicProfile(ObjectId accountID);

    /* Account Modification */
    Account updateUserProfile(String username, String field, String value);
    Account addSkill(String username, String skillName, String skillLevel);
    Account removeSkill(String username, String skillName);
    String deleteAccount(String username);

    /* Account Searching */
    Map<String, ? extends Searchable> searchWithFilter(String type, String query, int level, boolean suggestions);
    List<Account> searchByFirstName(String firstName);
    List<Account> searchByLastName(String lastName);
    List<Account> searchByFullName(String firstName, String lastName);
    List<Account> searchByFullName(String fullName);
    List<Account> searchByReputation(int minimumRep, boolean includeSuggested);
    List<Account> searchBySkill(String skillName, int minimumLevel);
    Account searchByUsername(String username);
    List<Group> searchByGroupName(String groupName);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    List<Skill> getAllValidSkills();
    List<Skill> getAllUserSkills(String username);
    List<Group> getAllUserGroups(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    List<Message> getNewMessages(String username);
    Message sendMessage(String username, ObjectId receiverID, String content, int type);

    /* Group centered services */
    Group createGroup(String username, String groupName, String bio);
    String deleteGroup(String username, ObjectId groupID);
    Account joinGroup(String username, ObjectId groupID);
    Account leaveGroup(String username, ObjectId groupID);
    Account inviteToGroup(String username, ObjectId accountID, ObjectId groupID);

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
    List<Account> moveSuggestedToTop(List<Account> accounts, int reputation);
    double getReputationRanking(String username);
}
