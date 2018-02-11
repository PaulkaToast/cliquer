
package com.styxxco.cliquer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String>
{
	public Message findByMessageID(String messageID);
}
