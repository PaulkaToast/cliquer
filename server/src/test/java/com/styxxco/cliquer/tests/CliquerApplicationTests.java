package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Skill;
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

	@Test
	public void testDatabase() {
		accountRepository.deleteAll();

		Account jordan = new Account("reed226", "Jordan", "Reed");
		Account shawn = new Account("montgo38", "Shawn", "Montgomery");
		String id = shawn.getAccountID();

		accountRepository.save(jordan);
		accountRepository.save(shawn);

		Account user = accountRepository.findByUsername("reed226");
		assertEquals("Reed", user.getLastName());
		user = accountRepository.findByAccountID(id);
		assertEquals("Shawn", user.getFirstName());
	}

	public void testAccountRetreival()
	{
		accountRepository.deleteAll();
		AccountServiceImp service = new AccountServiceImp(accountRepository, skillRepository, messageRepository, groupRepository);

		Account jordan = service.createAccount("reed226", "Jordan", "Reed");
		assertNotNull(jordan);

		Account shawn = service.createAccount("montgo38", "Shawn", "Montgomery");
		assertNotNull(shawn);

		Account retreive = service.getUserProfile(shawn.getUsername());
		assertEquals("Shawn", retreive.getFirstName());

		retreive = service.getUserProfile("reed");
		assertNull(retreive);

		retreive = service.getMemberProfile(shawn.getAccountID());
		assertNull(retreive.getAccountID());
		assertEquals("Montgomery", shawn.getLastName());

		jordan.setPublic(true);
		jordan.addSkill(new Skill("Lifting", 1));
		accountRepository.save(jordan);
		retreive = service.getPublicProfile(jordan.getAccountID());
		assertEquals(1, retreive.getSkill("Lifting").getSkillLevel());

		shawn.setPublic(false);
		shawn.addSkill(new Skill("Programming", 8));
		accountRepository.save(shawn);
		retreive = service.getPublicProfile(shawn.getAccountID());
		assertNull(retreive.getSkills());
	}

}
