
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface GroupRepository extends MongoRepository<Group, String>
{
	Group findByGroupID(ObjectId groupID);
	ArrayList<Group> findByGroupName(String groupName);
	ArrayList<Group> findByGroupLeaderID(ObjectId accountID);
}
