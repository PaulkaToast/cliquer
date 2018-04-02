package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.domain.Message.Types;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import com.styxxco.cliquer.service.impl.GroupServiceImpl;
import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class SprintThreeServicesTest {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public SkillRepository skillRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public GroupRepository groupRepository;

    public AccountService accountService;

    public GroupService groupService;

    /* Back end Unit Test for User Story 11 */
    @Test
    public void testGroupEventBroadcast()
    {
        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        Group hoops = groupService.createGroup(
                "Hoops",
                "To play basketball",
                shawn.getAccountID());

        Skill java = skillRepository.findBySkillNameAndSkillLevel("Java", 6);
        Skill ball = skillRepository.findBySkillNameAndSkillLevel("Basketball", 7);

        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        cliquer.addSkillReq(java);
        cliquer.setProximityReq(1);
        groupRepository.save(cliquer);

        hoops.addSkillReq(ball);
        hoops.setProximityReq(1);
        groupRepository.save(cliquer);

        jordan.setLatitude(40.0);
        jordan.setLongitude(-80.0);
        jordan.addSkill(java);
        accountRepository.save(jordan);

        shawn.setLatitude(40.2);
        shawn.setLongitude(-80.4);
        shawn.addSkill(java);
        shawn.addSkill(ball);
        accountRepository.save(shawn);

        kevin.setLatitude(40.4);
        kevin.setLongitude(-80.8);
        kevin.addSkill(ball);
        accountRepository.save(kevin);

        List<Account> result = groupService.broadcastEvent(cliquer.getGroupID(), jordan.getAccountID(),
                "Test run for Cliquer", 200, new ArrayList<>());
        assertEquals(1, result.size());
        assertEquals("Kevin", result.get(0).getFirstName());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().keySet().size());

        result = groupService.broadcastEvent(cliquer.getGroupID(), jordan.getAccountID(),
                "Test run for Cliquer", 30, new ArrayList<>());
        assertEquals(0, result.size());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(1, kevin.getMessageIDs().keySet().size());

        List<String> reqs = new ArrayList<>();
        reqs.add(ball.getSkillName());
        result = groupService.broadcastEvent(hoops.getGroupID(), shawn.getAccountID(),
                "Basketball tournament", 100, reqs);
        assertEquals(1, result.size());
        assertEquals("Kevin", result.get(0).getFirstName());
        kevin = accountRepository.findByAccountID(kevin.getAccountID());
        assertEquals(2, kevin.getMessageIDs().keySet().size());
    }

    /* Populates valid skills into database, in case they were deleted */
    @Before
    public void populateSkills()
    {
        accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        accountService.addSkillToDatabase("Java");
        accountService.addSkillToDatabase("JavaScript");
        accountService.addSkillToDatabase("C");
        accountService.addSkillToDatabase("C++");
        accountService.addSkillToDatabase("Python");
        accountService.addSkillToDatabase("C#");
        accountService.addSkillToDatabase("Ruby");
        accountService.addSkillToDatabase("Pascal");
        accountService.addSkillToDatabase("ARM");
        accountService.addSkillToDatabase("x86");
        accountService.addSkillToDatabase("Verilog");
        accountService.addSkillToDatabase("VIM");
        accountService.addSkillToDatabase("Microsoft Word");
        accountService.addSkillToDatabase("Google Sheets");
        accountService.addSkillToDatabase("Swift");
        accountService.addSkillToDatabase("Real Time Strategy Games");
        accountService.addSkillToDatabase("Role-Playing Games");
        accountService.addSkillToDatabase("Board Games");
        accountService.addSkillToDatabase("Platformer Games");
        accountService.addSkillToDatabase("Massively Multiplayer Online Role-Playing Games");
        accountService.addSkillToDatabase("Basketball");
        accountService.addSkillToDatabase("Lifting");
        accountService.addSkillToDatabase("Football");
        accountService.addSkillToDatabase("Volleyball");
        accountService.addSkillToDatabase("Baseball");
        accountService.addSkillToDatabase("Soccer");
        accountService.addSkillToDatabase("Tennis");
        accountService.addSkillToDatabase("Really Long Skill Name That Likely Needs To Be Shortened When It Is Shown On The Front End");
    }

    /* Function to clear items that should not already be in database */
    @After
    public void clearDatabase()
    {
        accountRepository.deleteAll();
        skillRepository.deleteAll();
        messageRepository.deleteAll();
        groupRepository.deleteAll();
    }
}
