
package com.styxxco.cliquer.domain;

import org.springframework.data.annotation.Id;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

@lombok.Getter
@lombok.Setter
@lombok.ToString(exclude = {"skillID"})

public class Skill
{
	@Id private String skillID;
	private String skillName;
	private int skillLevel;

	public Skill(String name, int level)			/* Constructor for creating a skill for an account	*/
	{
		this.skillName = name;
		this.skillLevel = level;
	}
}
