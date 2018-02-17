package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public interface AccountService {
    /* Account Creation */
    Account createAccount(String username, String firstName, String lastName);

    /* Account Access */
    Account getUserProfile(String username);
    Account getMemberProfile(ObjectId accountID);
    Account getPublicProfile(ObjectId accountID);

    /* Account Modification */
    Account updateUserProfile(String username, String field, String value);
    Account addSkill(String username, String skillName, int skillLevel);
    Account removeSkill(String username, String skillName);

    /* Skill centered services */
    Skill addSkillToDatabase(String skillName);
    ArrayList<Skill> getAllValidSkills();
    ArrayList<Skill> getAllUserSkills(String username);
    Skill getSkill(String username, String skillName);

    /* Message centered services */
    ArrayList<Message> getNewMessages(String username);
}
