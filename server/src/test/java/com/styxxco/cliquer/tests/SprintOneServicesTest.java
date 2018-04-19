package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import com.styxxco.cliquer.service.impl.GroupServiceImpl;
import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.bson.types.ObjectId;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class SprintOneServicesTest {

	@Autowired
	public AccountRepository accountRepository;
	@Autowired
	public SkillRepository skillRepository;
	@Autowired
	public MessageRepository messageRepository;
	@Autowired
	public GroupRepository groupRepository;
	@Autowired
	public GroupService groupService;
	@Autowired
	public AccountService accountService;
	@Autowired
	public RoleRepository roleRepository;

	/* Test basic storage of data in MongoDB */
	@Test
	public void testDatabase() {
		Account jordan = new Account("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = new Account("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
		String id = shawn.getAccountID();

		accountRepository.save(jordan);
		accountRepository.save(shawn);

		Account user = accountRepository.findByUsername("reed226");
		assertEquals("Reed", user.getLastName());
		user = accountRepository.findByAccountID(id);
		assertEquals("Shawn", user.getFirstName());
	}

	/* Test account creation and retrieval accountServices */
	@Test
	public void testAccountCreationAndRetrieval() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		assertNotNull(jordan);

		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
		assertNotNull(shawn);

		Account retrieve = accountService.getUserProfile(shawn.getUsername());
		assertEquals("Shawn", retrieve.getFirstName());

		retrieve = accountService.getUserProfile("reed");
		assertNull(retrieve);

		retrieve = accountService.getMemberProfile(shawn.getUsername());
		assertNull(retrieve.getPassword());
		assertEquals("Montgomery", shawn.getLastName());

		jordan.setPublic(true);
		Skill test = new Skill("Test1", 5);
		jordan.addSkill(test);
		accountRepository.save(jordan);
		retrieve = accountService.getPublicProfile(jordan.getUsername());
		assertEquals("Test1", retrieve.getSkillIDs().get(test.getSkillID()));

		shawn.setPublic(false);
		test = new Skill("Test2", 7);
		shawn.addSkill(test);
		accountRepository.save(shawn);
		retrieve = accountService.getPublicProfile(shawn.getUsername());
		assertNull(retrieve.getSkillIDs());
	}

	/* Test account modification accountServices */
	@Test
	public void testAccountModification() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");

		Skill programming = new Skill("Programming", 0);
		skillRepository.save(programming);
		Skill skill = accountService.addSkill("reed226", "Lifter", "1");
		assertNull(skill);
		Account retrieve = accountService.getUserProfile("reed226");
		assertEquals(0, retrieve.getSkillIDs().size());

		Skill boardgame = new Skill("Board Gaming", 0);
		skillRepository.save(boardgame);
		accountService.addSkill("montgo38", "Programming", "8");
		accountService.addSkill("montgo38", "Board Gaming", "6");
		skill = accountService.getSkill("montgo38", "Programming");
		assertEquals("Programming", skill.getSkillName());
		skill = accountService.getSkill("montgo38", "Board Gaming");
		assertEquals(6, skill.getSkillLevel());

		String skillID = skill.getSkillID();
        accountService.removeSkill("montgo38", "Board Gaming");
		skill = accountService.getSkill("montgo38", "Board Gaming");
		assertNull(skill);
		skill = skillRepository.findBySkillID(skillID);
		assertNotNull(skill);
	}

	/* Test account searching accountServices and ranking */
	@Test
	public void testAccountSearchingAndRanking() {
		Account reed = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account buckmaster = accountService.createAccount("buckmast", "buckmast@pdue.edu","Jordan", "Buckmaster");
		Account rhys = accountService.createAccount("rbuckmas", "rbuckmas@pdue.edu",  "Rhys", "Buckmaster");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");

		reed.setReputation(7);
		buckmaster.setReputation(69);
		rhys.setReputation(5);
		shawn.setReputation(6);

		reed.setPublic(true);
		buckmaster.setPublic(true);
		rhys.setPublic(true);
		shawn.setPublic(true);

		accountRepository.save(reed);
		accountRepository.save(buckmaster);
		accountRepository.save(rhys);
		accountRepository.save(shawn);

		Skill programming = new Skill("Programming", 0);
		skillRepository.save(programming);
        accountService.addSkill("reed226", "Programming", "7");
		Skill skill = accountService.addSkill("buckmast", "Programming", "-2");
		assertNull(skill);
        accountService.addSkill("buckmast", "Programming", "8");
        accountService.addSkill("rbuckmas", "Programming", "4");
        accountService.addSkill("montgo38", "Programming", "7");


		List<Account> search = accountService.searchByFirstName("Jordan");
		assertEquals(2, search.size());
		assertEquals("Buckmaster", search.get(0).getLastName()); //Buckmaster since comparator sorts by last name

		search = accountService.searchByFirstName("jordan");
		assertEquals(2, search.size());
		assertEquals("Buckmaster", search.get(0).getLastName()); //Buckmaster since comparator sorts by last name

		search = accountService.searchByLastName("Buckmaster");
		assertEquals(2, search.size());
        assertEquals("Rhys", search.get(1).getFirstName());

		Map<String, Account> searchMap = accountService.searchByFullName("Jordan", "Buckmaster");
		assertEquals(1, searchMap.size());

		assertEquals("Jordan", searchMap.get(buckmaster.getUsername()).getFirstName());
		assertEquals("Buckmaster", searchMap.get(buckmaster.getUsername()).getLastName());

		searchMap = accountService.searchByFullName("jOrDan", "bUckMaster");
		assertEquals(1, searchMap.size());

		assertEquals("Jordan", searchMap.get(buckmaster.getUsername()).getFirstName());
		assertEquals("Buckmaster", searchMap.get(buckmaster.getUsername()).getLastName());

		search = accountService.searchByReputation(6, false, false);
		assertEquals(3, search.size());
		assertEquals("Shawn", search.get(2).getFirstName());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = accountService.searchBySkill("Programming");
		assertEquals(4, search.size());

		assertEquals(new Double(75.0), new Double(accountService.getReputationRanking("reed226")));
		assertEquals(new Double(100.0), new Double(accountService.getReputationRanking("buckmast")));
		assertEquals(new Double(25.0), new Double(accountService.getReputationRanking("rbuckmas")));
		assertEquals(new Double(50.0), new Double(accountService.getReputationRanking("montgo38")));
	}

	/* Test group retrieval accountServices */
	@Test
	public void testGroupRetrieval() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

		Group cliquer = groupService.createGroup(
				"Cliquer",
				"To create a web app that facilitates the teaming of people who may have never met before",
				jordan.getAccountID());
		cliquer.addGroupMember(shawn);
		groupRepository.save(cliquer);

		Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertEquals(jordan.getAccountID(), retrieve.getGroupLeaderID());

		retrieve = groupService.getUserGroup(cliquer.getGroupID(), kevin.getAccountID());
		assertNull(retrieve);

		cliquer.setPublic(true);
		groupRepository.save(cliquer);
		retrieve = groupService.getPublicGroup(cliquer.getGroupID());
		assertEquals(jordan.getAccountID(), retrieve.getGroupLeaderID());

		cliquer.setPublic(false);
		groupRepository.save(cliquer);
		retrieve = groupService.getPublicGroup(cliquer.getGroupID());
		assertNull(retrieve);
	}

	@Test
    public void testRetrieveAllGroups() {
        Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");

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

        List<Group> groupsOne = accountService.getAllUserGroups(jordan.getUsername());
        List<Group> groupsTwo = accountService.getAllUserGroups(jordan.getUsername());
        assertEquals(3, groupsOne.size());
        for(int i = 0; i < groupsOne.size(); i++)
        {
            assertEquals(groupsOne.get(i).getGroupID(), groupsTwo.get(i).getGroupID());
        }
    }

	/* Test group modification accountServices */
	@Test
	public void testGroupModification() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

		Group cliquer = groupService.createGroup(
				"Cliquer",
				"To create a web app that facilitates the teaming of people who may have never met before",
				jordan.getAccountID());
		cliquer.addGroupMember(shawn);
		groupRepository.save(cliquer);

		Group modify = groupService.updateGroupSettings(cliquer.getGroupID(), shawn.getAccountID(), "reputationReq", "0.6");
		assertNull(modify);

		modify = groupService.updateGroupSettings(cliquer.getGroupID(), jordan.getAccountID(), "reputationReq", "0.6");
		assertEquals(new Double(0.6), new Double(modify.getReputationReq()));
		Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertEquals(new Double(0.6), new Double(retrieve.getReputationReq()));


		Skill programming = accountService.addSkillToDatabase("Programming");
		Skill lifter = accountService.addSkillToDatabase("Lifter");
		groupService.addSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming", 5);
		Skill skill = groupService.getSkillReq(cliquer.getGroupID(), "Lifter");
		assertNull(skill);
		skill = groupService.getSkillReq(cliquer.getGroupID(), "Programming");
		assertEquals("Programming", skill.getSkillName());
		assertEquals(5, skill.getSkillLevel());

		groupService.removeSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming");
		skill = groupService.getSkillReq(cliquer.getGroupID(), "Programming");
		assertNull(skill);

		modify = groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());
		List<String> memberIDs = new ArrayList<>(modify.getGroupMemberIDs().keySet());
		Account account = accountService.getMemberProfile(accountRepository.findByAccountID(memberIDs.get(2)).getUsername());
		assertEquals("Kevin", account.getFirstName());
		assertEquals(3, modify.getGroupMemberIDs().size());
		assertEquals(cliquer.getGroupName(), account.getGroupIDs().get(cliquer.getGroupID()));

		modify = groupService.removeGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
		memberIDs = new ArrayList<>(modify.getGroupMemberIDs().keySet());
		account = accountService.getMemberProfile(accountRepository.findByAccountID(memberIDs.get(1)).getUsername());
		assertEquals("Nagar", account.getLastName());
		assertEquals(2, modify.getGroupMemberIDs().size());
		account = accountService.getUserProfile(shawn.getUsername());
		assertEquals(0, account.getGroupIDs().size());

		account = accountService.leaveGroup(kevin.getUsername(), cliquer.getGroupID());
		assertEquals(0, account.getGroupIDs().size());
		retrieve = groupService.getUserGroup(cliquer.getGroupID(), jordan.getAccountID());
		memberIDs = new ArrayList<>(modify.getGroupMemberIDs().keySet());
		account = accountService.getMemberProfile(accountRepository.findByAccountID(memberIDs.get(0)).getUsername());
		assertEquals("Jordan", account.getFirstName());
		assertEquals(1, retrieve.getGroupMemberIDs().size());
	}

	@Test
	public void testAccountMessaging() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");

		Message first = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Be my friend?", Message.Types.FRIEND_INVITE);
		Message second = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Please be my friend?", Message.Types.FRIEND_INVITE);

		List<Message> newMessages = accountService.getMessages(shawn.getAccountID(), "false", null);
		assertEquals(2, newMessages.size());
		assertEquals(1, newMessages.get(0).getType());

		accountService.readMessage(shawn.getAccountID(), first.getMessageID());
		accountService.readMessage(shawn.getAccountID(), second.getMessageID());

		Message third = accountService.sendMessage(jordan.getAccountID(), shawn.getAccountID(), "Pretty please be my friend?", Message.Types.FRIEND_INVITE);

		newMessages = accountService.getMessages(shawn.getAccountID(), "false", null);
		assertEquals(1, newMessages.size());
		assertEquals("Pretty please be my friend?", newMessages.get(0).getContent());
	}

	@Test
	public void testAccountDeletion() {
		Account jordan = accountService.createAccount("reed226", "reed226@pdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@pdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@pdue.edu", "Kevin", "Nagar");

		Group cliquer = groupService.createGroup(
				"Cliquer",
				"To create a web app that facilitates the teaming of people who may have never met before",
				jordan.getAccountID());
		groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
		groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());

		Group hoops = groupService.createGroup(
				"Hoops",
				"Play ball",
				kevin.getAccountID());
		groupService.addGroupMember(hoops.getGroupID(), kevin.getAccountID(), jordan.getAccountID());

		Account deletedAccount = accountService.deleteAccount(jordan.getUsername());
		assertNotNull(deletedAccount);
		Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		Account account = accountRepository.findByAccountID(retrieve.getGroupLeaderID());
		assertEquals("Shawn", account.getFirstName());

		retrieve = groupService.getUserGroup(hoops.getGroupID(), kevin.getAccountID());
		assertEquals(1, retrieve.getGroupMemberIDs().size());

		account = accountRepository.findByUsername(jordan.getUsername());
		assertNull(account);

		Group deletedGroup = groupService.deleteGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertNotNull(deletedGroup);
		retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertNull(retrieve);

		account = accountRepository.findByUsername(shawn.getUsername());
		assertEquals(0, account.getGroupIDs().size());
		account = accountRepository.findByUsername(kevin.getUsername());
		assertEquals(1, account.getGroupIDs().size());
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
    public void clearDatabase()
    {
        accountRepository.deleteAll();
        skillRepository.deleteAll();
        messageRepository.deleteAll();
        groupRepository.deleteAll();
    }
}
