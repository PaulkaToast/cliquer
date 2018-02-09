
public class Converter
{
	private static final MongoClient mongo =
		new MongoClient(new MongoClientURI("mongodb://localhost:26074"));

	private static final DB database = mongoClient.getDB("Cliquer");

	public static DBObject toDBObject(Account account)
	{
		List<BasicDBObject> skillList = new ArrayList<>();
		for(Skill s : skills)
		{
			skillList.add(this.toDBObject(s));
		}
		return new BasicDBObject("accountID", account.getAccountID)
						.append("moderatorID", account.getModeratorID)
						.append("username", account.getUsername)
						.append("isPublic", account.getIsPublic)
						.append("reputationReq", account.getReputationReq)
						.append("proximityReq", account.getProximityReq)
						.append("reputation", account.getReputation)
						.append("skills", skillList)
						.append("groupIDs", account.getGroupIDs)
						.append("friendIDs", account.getFriendIDs)
						.append("messageIDs", account.getMessageIDs);
	}

	public static DBObject toDBObject(Skill skill)
	{
		return new BasicDBObject("skillID", skill.getSkillID)
						.append("skillName", skill.getSkillName)
						.append("skillLevel", skill.getSkillLevel);
	}

	public static DBCollection getCollection(String type)
	{
		return database.getCollection(type);
	}
}
