package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

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
    List<Account> searchByFirstName(String firstName);
    List<Account> searchByLastName(String lastName);
    List<Account> searchByFullName(String firstName, String lastName);
    List<Account> searchByFullName(String fullName);
    List<Account> searchByReputation(int minimumRep);
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
    Group deleteGroup(String groupid);
    Account joinGroup(String username, String groupid);
    Account leaveGroup(String username, String groupid);
    Account inviteToGroup(String username, String friendName, String groupid);
    Account addFriend(String username, String friendName);
    Account removeFriend(String username, String friendName);

    List<Role> getAnonRoles();
    List<Role> getUserRoles();
    List<Role> getModRoles();
    double getReputationRanking(String username);
}
