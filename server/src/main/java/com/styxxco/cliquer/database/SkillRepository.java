
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface SkillRepository extends MongoRepository<Skill, String>
{
	ArrayList<Skill> findBySkillName(String skillName);
	Skill findBySkillID(ObjectId skillID);
}
