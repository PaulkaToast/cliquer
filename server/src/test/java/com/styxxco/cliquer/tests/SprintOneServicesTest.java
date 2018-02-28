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
public class SprintOneServicesTest {

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
		Account jordan = new Account("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = new Account("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
		ObjectId id = shawn.getAccountID();

		accountRepository.save(jordan);
		accountRepository.save(shawn);

		Account user = accountRepository.findByUsername("reed226");
		assertEquals("Reed", user.getLastName());
		user = accountRepository.findByAccountID(id);
		assertEquals("Shawn", user.getFirstName());
		
		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
	}

	/* Test account creation and retrieval services */
	@Test
	public void testAccountCreationAndRetrieval()
	{
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		assertNotNull(jordan);

		Account shawn = service.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
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

		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
	}

	/* Test account modification services */
	@Test
	public void testAccountModification()
	{
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = service.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");

		Account modify = service.updateUserProfile("reed226", "firstName", "William");
		assertEquals("William", modify.getFirstName());
		Account retrieve = service.getUserProfile("reed226");
		assertEquals("William", retrieve.getFirstName());

		modify = service.updateUserProfile("montgo38", "accountID", new ObjectId().toString());
		assertNull(modify);
		retrieve = service.getUserProfile("montgo38");
		assertEquals(shawn.getAccountID(), retrieve.getAccountID());

		Skill programming = new Skill("Programming", 0);
		skillRepository.save(programming);
		modify = service.addSkill("reed226", "Lifting", "1");
		assertNull(modify);
		retrieve = service.getUserProfile("reed226");
		assertEquals(0, retrieve.getSkillIDs().size());

		Skill boardgame = new Skill("Board Gaming", 0);
		skillRepository.save(boardgame);
		service.addSkill("montgo38", "Programming", "8");
		service.addSkill("montgo38", "Board Gaming", "6");
		Skill skill = service.getSkill("montgo38", "Programming");
		assertEquals("Programming", skill.getSkillName());
		skill = service.getSkill("montgo38", "Board Gaming");
		assertEquals(6, skill.getSkillLevel());

		ObjectId skillID = skill.getSkillID();
		service.removeSkill("montgo38", "Board Gaming");
		skill = service.getSkill("montgo38", "Board Gaming");
		assertNull(skill);
		skill = skillRepository.findBySkillID(skillID);
		assertNull(skill);
		
		jordan = accountRepository.findByAccountID(jordan.getAccountID());
		shawn = accountRepository.findByAccountID(shawn.getAccountID());
		
		skillRepository.delete(programming);
		skillRepository.delete(boardgame);
		for(ObjectId id : jordan.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		for(ObjectId id : shawn.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
	}

	/* Test account searching services and ranking */
	@Test
	public void testAccountSearchingAndRanking()
	{
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account reed = service.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account buckmaster = service.createAccount("buckmast", "buckmast@purdue.edu","Jordan", "Buckmaster");
		Account rhys = service.createAccount("rbuckmas", "rbuckmas@purdue.edu",  "Rhys", "Buckmaster");
		Account shawn = service.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");

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
		service.addSkill("reed226", "Programming", "7");
		Account test = service.addSkill("buckmast", "Programming", "-2");
		assertNull(test);
		service.addSkill("buckmast", "Programming", "8");
		service.addSkill("rbuckmas", "Programming", "4");
		service.addSkill("montgo38", "Programming", "7");


		List<Account> search = service.searchByFirstName("Jordan");
		assertEquals(2, search.size());
		assertNull(search.get(0).getUsername());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = service.searchByLastName("Buckmaster");
		assertEquals(2, search.size());
		assertEquals("Rhys", search.get(1).getFirstName());

		search = service.searchByFullName("Jordan", "Buckmaster");
		assertEquals(1, search.size());
		assertEquals("Jordan", search.get(0).getFirstName());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = service.searchByReputation(6);
		assertEquals(3, search.size());
		assertEquals("Shawn", search.get(2).getFirstName());
		assertEquals("Buckmaster", search.get(0).getLastName());

		search = service.searchBySkill("Programming", 7);
		assertEquals(3, search.size());
		assertEquals("Buckmaster", search.get(0).getLastName());
		assertEquals("Montgomery", search.get(1).getLastName());
		assertEquals("Reed", search.get(2).getLastName());


		search = service.searchBySkill("Programming", 9);
		assertEquals(true, search.isEmpty());

		assertEquals(new Double(75.0), new Double(service.getReputationRanking("reed226")));
		assertEquals(new Double(100.0), new Double(service.getReputationRanking("buckmast")));
		assertEquals(new Double(25.0), new Double(service.getReputationRanking("rbuckmas")));
		assertEquals(new Double(50.0), new Double(service.getReputationRanking("montgo38")));
		
		reed = accountRepository.findByAccountID(reed.getAccountID());
		buckmaster = accountRepository.findByAccountID(buckmaster.getAccountID());
		rhys = accountRepository.findByAccountID(rhys.getAccountID());
		shawn = accountRepository.findByAccountID(shawn.getAccountID());

		skillRepository.delete(programming);
		for(ObjectId id : reed.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		for(ObjectId id : buckmaster.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		for(ObjectId id : rhys.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		for(ObjectId id : shawn.getSkillIDs())
		{
			skillRepository.delete(id.toString());
		}
		accountRepository.delete(reed);
		accountRepository.delete(buckmaster);
		accountRepository.delete(rhys);
		accountRepository.delete(shawn);
	}

	/* Test group retrieval services */
	@Test
	public void testGroupRetrieval()
	{
		AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

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
		
		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
		accountRepository.delete(kevin);
		groupRepository.delete(cliquer);
	}

	@Test
    public void testRetrieveAllGroups()
    {
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

        List<Group> groupsOne = accountService.getAllUserGroups(jordan.getUsername());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Group> groupsTwo = accountService.getAllUserGroups(jordan.getUsername());
        for(int i = 0; i < groupsOne.size(); i++)
        {
            assertEquals(groupsOne.get(i).getGroupID(), groupsTwo.get(i).getGroupID());
        }
        
        accountRepository.delete(jordan);
		groupRepository.delete(cliquer);
		groupRepository.delete(hoops);
		groupRepository.delete(games);

    }

	/* Test group modification services */
	@Test
	public void testGroupModification()
	{
		AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

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


		Skill programming = accountService.addSkillToDatabase("Programming");
		Skill lifting = accountService.addSkillToDatabase("Lifting");
		groupService.addSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming", 5);
		Skill skill = groupService.getSkillReq(cliquer.getGroupID(), "Lifting");
		assertNull(skill);
		skill = groupService.getSkillReq(cliquer.getGroupID(), "Programming");
		assertEquals("Programming", skill.getSkillName());
		assertEquals(5, skill.getSkillLevel());

		groupService.removeSkillReq(cliquer.getGroupID(), jordan.getAccountID(), "Programming");
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

		account = accountService.leaveGroup(kevin.getUsername(), cliquer.getGroupID().toString());
		assertEquals(0, account.getGroupIDs().size());
		retrieve = groupService.getUserGroup(cliquer.getGroupID(), jordan.getAccountID());
		account = accountService.getMemberProfile(retrieve.getGroupMemberIDs().get(0));
		assertEquals("Jordan", account.getFirstName());
		assertEquals(1, retrieve.getGroupMemberIDs().size());

		cliquer = groupRepository.findByGroupID(cliquer.getGroupID());
		
		skillRepository.delete(programming);
		skillRepository.delete(lifting);
		for(ObjectId id : cliquer.getSkillReqs())
		{
			skillRepository.delete(id.toString());
		}
		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
		accountRepository.delete(kevin);
		groupRepository.delete(cliquer);
	}

	@Test
	public void testAccountMessaging()
	{
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = service.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");

		Message first = service.sendMessage("reed226", shawn.getAccountID(), "Be my friend?", 1);
		Message second = service.sendMessage("reed226", shawn.getAccountID(), "Please be my friend?", 1);

		List<Message> newMessages = service.getNewMessages("montgo38");
		assertEquals(2, newMessages.size());
		assertEquals(1, newMessages.get(0).getType());

		Message third = service.sendMessage("reed226", shawn.getAccountID(), "Pretty please be my friend?", 1);

		newMessages = service.getNewMessages("montgo38");
		assertEquals(1, newMessages.size());
		assertEquals("Pretty please be my friend?", newMessages.get(0).getContent());
		
		messageRepository.delete(first);
		messageRepository.delete(second);
		messageRepository.delete(third);
		accountRepository.delete(jordan);
		accountRepository.delete(shawn);
	}

	@Test
	public void testAccountAndGroupDeletion()
	{
		AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
		GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
		Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
		Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

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

		String result = accountService.deleteAccount(jordan.getUsername());
		assertNotNull(result);
		Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		Account account = accountRepository.findByAccountID(retrieve.getGroupLeaderID());
		assertEquals("Shawn", account.getFirstName());

		retrieve = groupService.getUserGroup(hoops.getGroupID(), kevin.getAccountID());
		assertEquals(1, retrieve.getGroupMemberIDs().size());

		account = accountRepository.findByUsername(jordan.getUsername());
		assertNull(account);

		result = groupService.deleteGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertNotNull(result);
		retrieve = groupService.getUserGroup(cliquer.getGroupID(), shawn.getAccountID());
		assertNull(retrieve);

		account = accountRepository.findByUsername(shawn.getUsername());
		assertEquals(0, account.getGroupIDs().size());
		account = accountRepository.findByUsername(kevin.getUsername());
		assertEquals(1, account.getGroupIDs().size());
		
		accountRepository.delete(shawn);
		accountRepository.delete(kevin);
		groupRepository.delete(hoops);
	}

	/* Populates valid skills into database, in case they were deleted */
	@Test
	public void populateSkills()
	{
		AccountService service = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

		service.addSkillToDatabase("Java");
		service.addSkillToDatabase("JavaScript");
		service.addSkillToDatabase("C");
		service.addSkillToDatabase("C++");
		service.addSkillToDatabase("Python");
		service.addSkillToDatabase("C#");
		service.addSkillToDatabase("Ruby");
		service.addSkillToDatabase("Pascal");
		service.addSkillToDatabase("ARM");
		service.addSkillToDatabase("x86");
		service.addSkillToDatabase("Verilog");
		service.addSkillToDatabase("VIM");
		service.addSkillToDatabase("Microsoft Word");
		service.addSkillToDatabase("Google Sheets");
		service.addSkillToDatabase("Swift");
		service.addSkillToDatabase("Real Time Strategy Games");
		service.addSkillToDatabase("Role-Playing Games");
		service.addSkillToDatabase("Board Games");
		service.addSkillToDatabase("Platformer Games");
		service.addSkillToDatabase("Massively Multiplayer Online Role-Playing Games");
		service.addSkillToDatabase("Basketball");
		service.addSkillToDatabase("Lifting");
		service.addSkillToDatabase("Football");
		service.addSkillToDatabase("Volleyball");
		service.addSkillToDatabase("Baseball");
		service.addSkillToDatabase("Soccer");
		service.addSkillToDatabase("Tennis");
		service.addSkillToDatabase("Really Long Skill Name That Likely Needs To Be Shortened When It Is Shown On The Front End");
	}



}
