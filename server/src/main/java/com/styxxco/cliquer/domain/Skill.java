
package com.styxxco.cliquer.domain;

/* Serves as the entity representing user and moderator data.	*/
/* Extended by the Moderator class								*/

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString(exclude = {"skillID"})

public class Skill extends Searchable implements Comparable<Skill> {
	@Id
	private final String skillID;

	private String skillName;
	private int skillLevel;

	public Skill(String skillName, int skillLevel)			/* Constructor for creating a skill for an account	*/
	{
		this.skillID = new ObjectId().toString();
		this.skillName = skillName;
		this.skillLevel = skillLevel;
	}

	@Override
	public int compareTo(Skill skill)
	{
		return this.skillName.compareTo(skill.skillName);
	}
}
