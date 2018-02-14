
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository<Account, String>
{
	public Account findByUsername(String username);
	public Account findByAccountID(String accountID);
}
