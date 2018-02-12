
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString(exclude = {"skillID"})

public class Skill
{
	@Id
	@Generated
	private String skillID;

	private String skillName;
	private int skillLevel;

	public Skill(String name, int level)			/* Constructor for creating a skill for an account	*/
	{
		this.skillName = name;
		this.skillLevel = level;
	}

}
