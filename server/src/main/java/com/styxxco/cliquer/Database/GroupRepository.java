
package com.styxxco.cliquer.Database;

public interface GroupRepository extends MongoRepository<Group, String>
{
	public Group findByGroupID(String groupID);
	public Group findByGroupName(String groupName);
	public Group findByGroupLeaderID(String accountID);
}
