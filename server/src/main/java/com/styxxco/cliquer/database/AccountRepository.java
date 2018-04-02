
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String>
{
	boolean existsByUsername(String username);
	Account findByUsername(String username);
	boolean existsByAccountID(String accountID);
	Account findByAccountID(String accountID);
	List<Account> findByFirstNameContainsIgnoreCase(String firstName);
	List<Account> findByFirstName(String firstName);
	List<Account> findByIsModeratorTrue();
	List<Account> findByLastNameContainsIgnoreCase(String firstName);
	List<Account> findByLastName(String firstName);
	List<Account> findByReputation(int reputation);
	List<Account> findBySkillIDs(String skillID);
}
