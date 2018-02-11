
package com.styxxco.cliquer;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SkillRepository extends MongoRepository<Skill, String>
{
	public Skill findBySkillName(String skillName);
}
