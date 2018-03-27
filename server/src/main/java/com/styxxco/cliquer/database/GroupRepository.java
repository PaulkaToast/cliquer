
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String>
{
	boolean existsByGroupID(String groupID);
	Group findByGroupID(String groupID);
	List<Group> findAllByGroupNameContainsIgnoreCase(String groupName);
	List<Group> findAll();
	List<Group> findAllByIsPublic(boolean isPublic);
	List<Group> findByGroupLeaderID(String accountID);
}
