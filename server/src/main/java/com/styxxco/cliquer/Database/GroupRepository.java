
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRepository extends MongoRepository<Group, String>
{
	public Group findByGroupID(String groupID);
	public Group findByGroupName(String groupName);
	public Group findByGroupLeaderID(String accountID);
}
