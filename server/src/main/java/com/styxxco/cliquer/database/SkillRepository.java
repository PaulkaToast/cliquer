
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SkillRepository extends MongoRepository<Skill, String>
{
	public Skill findBySkillName(String skillName);
}
