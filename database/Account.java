
/* Throwing together random database stuff until Spring is setup */

public class Account
{
	private long accountID;
	private long moderatorID;
	private String username;

	private boolean isPublic;		
	private double reputationReq;	/* Represents fraction of user rep */
	private int proximityReq;

	private int reputation;			
	private List<Skill> skills;
	
	private List<Long> groupIDs;
	private List<Long> friendIDs;
	private List<Long> messageIDs;

	public void storeData()
	{
		MongoClient mongo = new MongoClient(
							new MongoClientURI("mongodb://localhost:26074"));
		DB database = mongoClient.getDB("Cliquer");
		DBCollection collection = database.getCollection("Accounts");
		List<BasicDBObject> skillList = new ArrayList<>();
		for(Skill s : skills)
		{
			skillList.add(new BasicDBObject("skillID", s.getSkillID)
								.append("skillName", s.getSkillName)
								.append("skillLevel", s.getSkillLevel));
		}
		DBObject account = new BasicDBObject("accountID", this.accountID)
								.append("moderatorID", this.moderatorID)
								.append("username", this.username)
								.append("isPublic", this.isPublic)
								.append("reputationReq", this.reputationReq)
								.append("proximityReq", this.proximityReq)
								.append("reputation", this.reputation)
								.append("skills", skillList)
								.append("groupIDs", this.groupIDs)
								.append("friendIDs", this.friendIDs)
								.append("messageIDs", this.messageIDs);
		collection.insert(account);
	}
}
