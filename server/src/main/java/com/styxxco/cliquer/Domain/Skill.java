
package com.styxxco.cliquer.Domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@Getter
@Setter
@ToString(exclude = {"skillID"})

public class Skill
{
	@Id private final String skillID;

	private String skillName;
	private int skillLevel;

	public Skill(String name, int level)			/* Constructor for creating a skill for an account	*/
	{
		this.skillName = name;
		this.skillLevel = level;
	}
}
