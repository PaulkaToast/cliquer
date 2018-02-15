package com.styxxco.cliquer.service;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.RegisterUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

public interface AccountService extends UserDetailsService {
    Account createAccount(String username, String firstName, String lastName);

    Account getUserProfile(String username);
    Account updateUserProfile(String username, String field, String value);
    Account getMemberProfile(String accountID);
    Account getPublicProfile(String accountID);

    Account addSkill(String username, String skillName, int skillLevel);
    Account removeSkill(String username, String skillName);

    ArrayList<Message> getNewMessages(String username);

    Account registerUser(RegisterUser init);
}
