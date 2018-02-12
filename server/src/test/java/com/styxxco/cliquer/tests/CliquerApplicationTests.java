package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.AccountRepository;
import com.styxxco.cliquer.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CliquerApplicationTests {

	@Autowired
	public AccountRepository accountRepository;

	@Test
	public void contextLoads() {
		accountRepository.deleteAll();

		Account jordan = new Account("reed226", "Jordan", "Reed");
		Account shawn = new Account("montgo38", "Shawn", "Montgomery");

		accountRepository.save(jordan);
		accountRepository.save(shawn);

		accountRepository.findAll().forEach(System.out::println);
	}

}
