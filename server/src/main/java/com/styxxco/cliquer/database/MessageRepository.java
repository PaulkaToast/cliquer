
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String>
{
	boolean existsByMessageID(String messageID);
	Message findByMessageID(String messageID);
	List<Message> findBySenderID(String senderID);
	List<Message> findByParentID(String parentID);
	Message findByParentIDAndSenderID(String parentID);
}
