
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface GroupRepository extends MongoRepository<Group, String>
{
	boolean existsByGroupID(String groupID);
	Group findByGroupID(String groupID);
	ArrayList<Group> findAllByGroupName(String groupName);
	ArrayList<Group> findAllByPublic(boolean isPublic);
	ArrayList<Group> findByGroupLeaderID(String accountID);
}
