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

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CliquerApplicationTests {

	@Autowired
	public AccountRepository accountRepository;
	@Autowired
	public SkillRepository skillRepository;
	@Autowired
	public MessageRepository messageRepository;
	@Autowired
	public GroupRepository groupRepository;

	/* Test basic storage of data in MongoDB */
	@Test
	public void testDatabase() {
		accountRepository.deleteAll();

		Account jordan = new Account("reed226", "Jordan", "Reed");
		Account shawn = new Account("montgo38", "Shawn", "Montgomery");
		ObjectId id = shawn.getAccountID();

		accountRepository.save(jordan);
		accountRepository.save(shawn);

		Account user = accountRepository.findByUsername("reed226");
		assertEquals("Reed", user.getLastName());
		user = accountRepository.findByAccountID(id);
		assertEquals("Shawn", user.getFirstName());
	}

	/* Test account creation and retrieval services */
	@Test
	public void testAccountCreationAndRetrieval()
	{
		accountRepository.deleteAll();
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "Jordan", "Reed");
		assertNotNull(jordan);

		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");
		assertNotNull(shawn);

		Account retrieve = service.getUserProfile(shawn.getUsername());
		assertEquals("Shawn", retrieve.getFirstName());

		retrieve = service.getUserProfile("reed");
		assertNull(retrieve);

		retrieve = service.getMemberProfile(shawn.getAccountID());
		assertNull(retrieve.getUsername());
		assertEquals("Montgomery", shawn.getLastName());

		jordan.setPublic(true);
		ObjectId test = new ObjectId();
		jordan.addSkill(test);
		accountRepository.save(jordan);
		retrieve = service.getPublicProfile(jordan.getAccountID());
		assertEquals(test, retrieve.getSkillIDs().get(0));

		shawn.setPublic(false);
		test = new ObjectId();
		shawn.addSkill(test);
		accountRepository.save(shawn);
		retrieve = service.getPublicProfile(shawn.getAccountID());
		assertNull(retrieve.getSkillIDs());
	}

	/* Test account modification services */
	@Test
	public void testAccountModification()
	{
		accountRepository.deleteAll();
		skillRepository.deleteAll();
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "Jordan", "Reed");
		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");

		Account modify = service.updateUserProfile("reed226", "firstName", "William");
		assertEquals("William", modify.getFirstName());
		Account retrieve = service.getUserProfile("reed226");
		assertEquals("William", retrieve.getFirstName());

		modify = service.updateUserProfile("montgo38", "accountID", new ObjectId().toString());
		assertNull(modify);
		retrieve = service.getUserProfile("montgo38");
		assertEquals(shawn.getAccountID(), retrieve.getAccountID());

		Skill skill = new Skill("Programming", 0);
		skillRepository.save(skill);
		modify = service.addSkill("reed226", "Lifting", 1);
		assertNull(modify);
		retrieve = service.getUserProfile("reed226");
		assertEquals(0, retrieve.getSkillIDs().size());

		skill = new Skill("Board Gaming", 0);
		skillRepository.save(skill);
		service.addSkill("montgo38", "Programming", 8);
		service.addSkill("montgo38", "Board Gaming", 6);
		skill = service.getSkill("montgo38", "Programming");
		assertEquals("Programming", skill.getSkillName());
		skill = service.getSkill("montgo38", "Board Gaming");
		assertEquals(6, skill.getSkillLevel());

		ObjectId skillID = skill.getSkillID();
		service.removeSkill("montgo38", "Board Gaming");
		skill = service.getSkill("montgo38", "Board Gaming");
		assertNull(skill);
		skill = skillRepository.findBySkillID(skillID);
		assertNull(skill);
	}

	/* Test account searching services and ranking */
	@Test
	public void testAccountSearchingAndRanking()
	{
		accountRepository.deleteAll();
		skillRepository.deleteAll();
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account reed = service.createAccount("reed226", "Jordan", "Reed");
		Account buckmaster = service.createAccount("buckmast", "Jordan", "Buckmaster");
		Account rhys = service.createAccount("rbuckmas", "Rhys", "Buckmaster");
		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");

		reed.setReputation(5);
		buckmaster.setReputation(69);
		rhys.setReputation(5);
		shawn.setReputation(6);

		accountRepository.save(reed);
		accountRepository.save(buckmaster);
		accountRepository.save(rhys);
		accountRepository.save(shawn);

		Skill skill = new Skill("Programming", 0);
		skillRepository.save(skill);
		service.addSkill("reed226", "Programming", 7);
		Account test = service.addSkill("buckmast", "Programming", -2);
		assertNull(test);
		service.addSkill("buckmast", "Programming", 8);
		service.addSkill("rbuckmas", "Programming", 4);
		service.addSkill("montgo38", "Programming", 7);


		ArrayList<Account> search = service.searchByFirstName("Jordan");
		assertEquals(2, search.size());
		assertNull(search.get(0).getUsername());
		assertEquals("Jordan", search.get(0).getFirstName());

		search = service.searchByLastName("Buckmaster");
		assertEquals(2, search.size());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = service.searchByReputation(7);
		assertEquals(1, search.size());
		assertEquals("Jordan", search.get(0).getFirstName());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = service.searchBySkill("Programming", 7);
		assertEquals(3, search.size());
		assertNotEquals("Rhys", search.get(0).getFirstName());
		assertNotEquals("Rhys", search.get(1).getFirstName());
		assertNotEquals("Rhys", search.get(2).getFirstName());


		search = service.searchBySkill("Programming", 9);
		assertEquals(true, search.isEmpty());

		assertEquals(new Double(50.0), new Double(service.getReputationRanking("reed226")));
		assertEquals(new Double(100.0), new Double(service.getReputationRanking("buckmast")));
		assertEquals(new Double(50.0), new Double(service.getReputationRanking("rbuckmas")));
		assertEquals(new Double(75.0), new Double(service.getReputationRanking("montgo38")));
	}

	/* Test group retrieval services */
	@Test
	public void testGroupRetrieval()
	{
		accountRepository.deleteAll();
		skillRepository.deleteAll();
		groupRepository.deleteAll();
		AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = accountService.createAccount("reed226", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "Kevin", "Nagar");

		Group cliquer = groupService.createGroup(
				"Cliquer",
				"To create a web app that facilitates the teaming of people who may have never met before",
				jordan.getAccountID());
		cliquer.addGroupMember(shawn.getAccountID());
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
		assertNull(retrieve.getGroupMemberIDs());
	}

	/* Test group modification services */
	@Test
	public void testGroupModification()
	{
		accountRepository.deleteAll();
		skillRepository.deleteAll();
		groupRepository.deleteAll();
		AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = accountService.createAccount("reed226", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "Kevin", "Nagar");

		Group cliquer = groupService.createGroup(
				"Cliquer",
				"To create a web app that facilitates the teaming of people who may have never met before",
				jordan.getAccountID());
		cliquer.addGroupMember(shawn.getAccountID());
		groupRepository.save(cliquer);

		Group modify = groupService.updateGroupSettings(cliquer.getGroupID(), shawn.getAccountID(), "reputationReq", "0.6");
		assertNull(modify);

		modify = groupService.updateGroupSettings(cliquer.getGroupID(), jordan.getAccountID(), "reputationReq", "0.6");
		assertEquals(new Double(0.6), new Double(modify.getReputationReq()));
		Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertEquals(new Double(0.6), new Double(retrieve.getReputationReq()));


		accountService.addSkillToDatabase("Programming");
		accountService.addSkillToDatabase("Lifting");
		modify = groupService.addSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming", 5);
		Skill skill = groupService.getSkillReq(cliquer.getGroupID(), "Lifting");
		assertNull(skill);
		skill = groupService.getSkillReq(cliquer.getGroupID(), "Programming");
		assertEquals("Programming", skill.getSkillName());
		assertEquals(5, skill.getSkillLevel());

		modify = groupService.removeSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming");
		skill = groupService.getSkillReq(cliquer.getGroupID(), "Programming");
		assertNull(skill);

		modify = groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());
		Account account = accountService.getMemberProfile(modify.getGroupMemberIDs().get(2));
		assertEquals("Kevin", account.getFirstName());
		assertEquals(3, modify.getGroupMemberIDs().size());
		assertEquals(cliquer.getGroupID(), account.getGroupIDs().get(0));

		modify = groupService.removeGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
		account = accountService.getMemberProfile(modify.getGroupMemberIDs().get(1));
		assertEquals("Nagar", account.getLastName());
		assertEquals(2, modify.getGroupMemberIDs().size());
		account = accountService.getUserProfile(shawn.getUsername());
		assertEquals(0, account.getGroupIDs().size());

		account = accountService.leaveGroup(kevin.getUsername(), cliquer.getGroupID());
		assertEquals(0, account.getGroupIDs().size());
		retrieve = groupService.getUserGroup(cliquer.getGroupID(), jordan.getAccountID());
		account = accountService.getMemberProfile(retrieve.getGroupMemberIDs().get(0));
		assertEquals("Jordan", account.getFirstName());
		assertEquals(1, modify.getGroupMemberIDs().size());
	}

	@Test
	public void testAccountMessaging()
	{
		accountRepository.deleteAll();
		skillRepository.deleteAll();
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "Jordan", "Reed");
		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");

		Message first = service.sendMessage("reed226", shawn.getAccountID(), "Be my friend?", "Friend Invite");
		Message second = service.sendMessage("reed226", shawn.getAccountID(), "Please be my friend?", "Friend Invite");

		ArrayList<Message> newMessages = service.getNewMessages("montgo38");
		assertEquals(2, newMessages.size());
		assertEquals("Friend Invite", newMessages.get(0).getType());

		Message third = service.sendMessage("reed226", shawn.getAccountID(), "Pretty please be my friend?", "Friend Invite");

		newMessages = service.getNewMessages("montgo38");
		assertEquals(1, newMessages.size());
		assertEquals("Pretty please be my friend?", newMessages.get(0).getContent());
	}

	/* Stress test for creating skills. Also populates valid skills in database */
	@Test
	public void populateSkills()
	{
		skillRepository.deleteAll();
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		assertNotNull(service.addSkillToDatabase("Java"));
		assertNull(service.addSkillToDatabase("Java"));
		assertNull(service.addSkillToDatabase("Java"));

		assertNotNull(service.addSkillToDatabase("JavaScript"));
		assertNotNull(service.addSkillToDatabase("C"));
		assertNotNull(service.addSkillToDatabase("C++"));
		assertNotNull(service.addSkillToDatabase("Python"));
		assertNotNull(service.addSkillToDatabase("C#"));
		assertNull(service.addSkillToDatabase("C"));
		assertNotNull(service.addSkillToDatabase("Ruby"));
		assertNotNull(service.addSkillToDatabase("Pascal"));
		assertNotNull(service.addSkillToDatabase("ARM"));
		assertNotNull(service.addSkillToDatabase("x86"));
		assertNotNull(service.addSkillToDatabase("Verilog"));
		assertNotNull(service.addSkillToDatabase("VIM"));
		assertNotNull(service.addSkillToDatabase("Microsoft Word"));
		assertNotNull(service.addSkillToDatabase("Google Sheets"));
		assertNull(service.addSkillToDatabase("ARM"));
		assertNotNull(service.addSkillToDatabase("Swift"));
		assertNotNull(service.addSkillToDatabase("Real Time Strategy Games"));
		assertNotNull(service.addSkillToDatabase("Role-Playing Games"));
		assertNotNull(service.addSkillToDatabase("Board Games"));
		assertNotNull(service.addSkillToDatabase("Platformer Games"));
		assertNull(service.addSkillToDatabase("Real Time Strategy Games"));
		assertNotNull(service.addSkillToDatabase("Massively Multiplayer Online Role-Playing Games"));
		assertNull(service.addSkillToDatabase("Google Sheets"));
		assertNotNull(service.addSkillToDatabase("Basketball"));
		assertNotNull(service.addSkillToDatabase("Lifting"));
		assertNotNull(service.addSkillToDatabase("Football"));
		assertNotNull(service.addSkillToDatabase("Volleyball"));
		assertNotNull(service.addSkillToDatabase("Baseball"));
		assertNull(service.addSkillToDatabase("Basketball"));
		assertNotNull(service.addSkillToDatabase("Soccer"));
		assertNotNull(service.addSkillToDatabase("Tennis"));
		assertNull(service.addSkillToDatabase("Ruby"));
		assertNull(service.addSkillToDatabase("Pascal"));
		assertNull(service.addSkillToDatabase("ARM"));
		assertNull(service.addSkillToDatabase("x86"));
		assertNull(service.addSkillToDatabase("Verilog"));
		assertNull(service.addSkillToDatabase("VIM"));
		assertNull(service.addSkillToDatabase("Microsoft Word"));
		assertNull(service.addSkillToDatabase("Google Sheets"));
		assertNotNull(service.addSkillToDatabase("Really Long Skill Name That Likely Needs To Be Shortened When It Is Shown On The Front End"));

		ArrayList<Skill> skills = service.getAllValidSkills();
		System.out.println(skills);
	}



}
