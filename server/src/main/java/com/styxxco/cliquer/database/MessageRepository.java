
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String>
{
	Message findByMessageID(ObjectId messageID);
}
