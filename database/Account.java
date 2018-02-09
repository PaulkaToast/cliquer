
/* Throwing together random database stuff until Spring is setup */

public class Account
{
	private long accountID;
	private long moderatorID;		/* 0 if not a moderator				*/
	private String username;		/* Must be unique					*/

	private boolean isPublic;		
	private double reputationReq;	/* Represents fraction of user rep 	*/
	private int proximityReq;

	private int reputation;			
	private List<Skill> skills;
	
	private List<Long> groupIDs;
	private List<Long> friendIDs;
	private List<Long> messageIDs;

	public Account(String name, boolean create)
	{
		DBCollection collection = Converter.getCollection("Accounts");

		DBObject query = new BasicDBObject("username", name);
		DBCursor cursor = collection.find(query);
		if(create && cursor == null)
		{
			this.username = name;
			this.storeData();	
		}
		else if(cursor != null)
		{
			DBObject accountData = cursor.one();

			this.accountID = (long)cursor.one().get("accountID");
			this.moderatorID = (long)cursor.one().get("moderatorID");
			this.username = (String)cursor.one().get("username");

			this.isPublic = (boolean)cursor.one().get("isPublic");		
			this.reputationReq = (double)cursor.one().get("reputationReq");
			this.proximityReq = (int)cursor.one().get("proximityReq");

			DBObject skillData = (List<DBObject>)cursor.one().get("skillList");
			this.skills;
			this.reputation = (int)cursor.one().get("reputation");			
	
			this.groupIDs;
			this.friendIDs;
			this.messageIDs;
		}
		else
		{
			this.accountID = 0;
		}
	}
		

	public void storeData()
	{
		DBCollection collection = Converter.getCollection("Accounts");
		collection.insert(Converter.toDBObject(this));
	}
}
