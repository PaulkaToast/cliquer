
public class Skill
{
	private long skillID;
	private String skillName;
	private int skillLevel;

	public Skill(long id, String name, int level)	/* Constructor for skill retreived from database 	*/
	{
	}

	public Skill(String name, int level)			/* Constructor for creating a skill for an account	*/
	{
		DBCollection collection = Converter.getCollection("Skills");
		
		DBObject query = new BasicDBObject("skillName", name);
		DBCursor cursor = collection.find(query);
		
		if(cursor == null)
		{
			this.skillID = 0;
		}
		else
		{
			this.skillID = (long)cursor.one().get("skillID");
			this.skillName = name;
			this.skillLevel = level;
		}
		
	}
}
