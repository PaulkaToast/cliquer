
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString(exclude = {"skillID"})

public class Skill
{
	@Id
	private final ObjectId skillID;

	private String skillName;
	private int skillLevel;

	public Skill(String name, int level)			/* Constructor for creating a skill for an account	*/
	{
		this.skillID = new ObjectId();
		this.skillName = name;
		this.skillLevel = level;
	}

}
