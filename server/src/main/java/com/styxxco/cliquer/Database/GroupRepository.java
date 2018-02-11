
package com.styxxco.cliquer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRepository extends MongoRepository<Group, String>
{
	public Group findByGroupID(String groupID);
	public Group findByGroupName(String groupName);
	public Group findByGroupLeaderID(String accountID);
}
