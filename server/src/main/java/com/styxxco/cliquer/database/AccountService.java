package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public interface AccountService {
    Account createAccount(String username, String firstName, String lastName);

    Account getUserProfile(String username);
    Account updateUserProfile(String username, String field, String value);
    Account getMemberProfile(ObjectId accountID);
    Account getPublicProfile(ObjectId accountID);

    Account addSkill(String username, String skillName, int skillLevel);
    Account removeSkill(String username, String skillName);

    ArrayList<Message> getNewMessages(String username);
}
