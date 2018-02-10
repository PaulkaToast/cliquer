
package com.styxxco.cliquer.Database;

public interface MessageRepository extends MongoRepository<Message, String>
{
	public Message findByMessageID(String messageID);
}
