
package com.styxxco.cliquer.Database;

public interface AccountRepository extends MongoRepository<Account, String>
{
	public Account findByUsername(String username);
}
