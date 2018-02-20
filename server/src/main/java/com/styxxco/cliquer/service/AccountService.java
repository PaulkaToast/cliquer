package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.*;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public interface AccountService extends UserDetailsService {
    /* Account Creation */
    Account registerUser(RegisterUser init);
    Account createAccount(String username, String firstName, String lastName);

    /* Account Access */
    Account getUserProfile(String username);
    Account getMemberProfile(ObjectId accountID);
    Account maskPublicProfile(Account account);
    Account getPublicProfile(ObjectId accountID);

    /* Account Modification */
    Account updateUserProfile(String username, String field, String value);
    Account addSkill(String username, String skillName, int skillLevel);
    Account removeSkill(String username, String skillName);

    /* Account Searching */
    ArrayList<Account> searchByFirstName(String firstName);
    ArrayList<Account> searchByLastName(String lastName);
    ArrayList<Account> searchByReputation(int minimumRep);
    ArrayList<Account> searchBySkill(String skillName, int minimumLevel);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    ArrayList<Skill> getAllValidSkills();
    ArrayList<Skill> getAllUserSkills(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    ArrayList<Message> getNewMessages(String username);
    Message sendMessage(String username, ObjectId receiverID, String content, String type);

    /* Group centered services */
    Account joinGroup(String username, ObjectId groupID);
    Account leaveGroup(String username, ObjectId groupID);

    List<Role> getAnonRoles();
    double getReputationRanking(String username);
}
