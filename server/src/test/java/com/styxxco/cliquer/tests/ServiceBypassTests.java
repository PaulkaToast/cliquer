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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class ServiceBypassTests {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public SkillRepository skillRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public GroupRepository groupRepository;
    @Autowired
    public RoleRepository roleRepository;
    @Autowired
    public AccountService accountService;
    @Autowired
    public GroupService groupService;

    /* Test that developer accounts are automatically moderators */
    @Test
    public void testModeratorBypass() {
        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");
        Account paula = accountService.createAccount("toth21", "toth21@purdue.edu", "Paula", "Toth");

        assertEquals(true, jordan.isModerator());
        assertEquals(true, shawn.isModerator());
        assertEquals(true, kevin.isModerator());
        assertEquals(true, buckmaster.isModerator());
        assertEquals(true, paula.isModerator());
    }

    /* Test that moderators can directly edit protected account fields */
    @Test
    public void testAccountModificationBypass() {
        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

        Account result = accountService.editUserProfile(jordan.getAccountID(), shawn.getAccountID(), "reputation", "54");
        assertEquals(54, result.getReputation());
        shawn = accountRepository.findByAccountID(shawn.getAccountID());
        assertEquals(54, shawn.getReputation());

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "latitude", "20.0");
        assertEquals(new Double(20.0), new Double(result.getLatitude()));

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "longitude", "40.0");
        assertEquals(new Double(40.0), new Double(result.getLongitude()));

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "suspendTime", "1");
        assertEquals(1, result.getSuspendTime());

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "loggedInTime", "1440");
        assertEquals(1440, result.getLoggedInTime());

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "isModerator", "true");
        assertEquals(true, result.isModerator());

        result = accountService.editUserProfile(jordan.getAccountID(), kevin.getAccountID(), "isNewUser", "false");
        assertEquals(false, result.isNewUser());
    }

    /* Populates valid skills into database, in case they were deleted */
    @Before
    public void populateSkills() {
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
    public void clearDatabase() {
        accountRepository.deleteAll();
        skillRepository.deleteAll();
        messageRepository.deleteAll();
        groupRepository.deleteAll();
    }
}
