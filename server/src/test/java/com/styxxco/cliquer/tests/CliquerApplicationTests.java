package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

	/* Test account retrieval services */
	@Test
	public void testAccountRetrieval()
	{
		accountRepository.deleteAll();
		AccountServiceImp service = new AccountServiceImp(accountRepository, skillRepository, messageRepository, groupRepository);

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
		AccountServiceImp service = new AccountServiceImp(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "Jordan", "Reed");
		assertNotNull(jordan);

		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");
		assertNotNull(shawn);
		
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

	/* Test group retrieval services */
	@Test
	public void testGroupRetrieval()
	{
		accountRepository.deleteAll();
		groupRepository.deleteAll();
		AccountServiceImp accountService = new AccountServiceImp(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupServiceImp groupService = new GroupServiceImp(accountRepository, skillRepository, messageRepository, groupRepository);

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


}
