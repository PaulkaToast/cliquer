package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import com.styxxco.cliquer.service.impl.GroupServiceImpl;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SprintTwoServicesTest {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public SkillRepository skillRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public GroupRepository groupRepository;

    /* Test group searching filters and filter chaining */
    //@Test
    public void testGroupSearching() {
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        Group hoops = groupService.createGroup(
                "Hoops",
                "To play basketball",
                jordan.getAccountID());
        Group games = groupService.createGroup(
                "Games",
                "To play games",
                jordan.getAccountID());

        accountRepository.delete(jordan);
        groupRepository.delete(cliquer);
        groupRepository.delete(hoops);
        groupRepository.delete(games);
    }
}
