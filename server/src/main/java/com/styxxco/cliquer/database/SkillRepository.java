
package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Skill;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;

public interface SkillRepository extends MongoRepository<Skill, String>
{
	boolean existsBySkillName(String skillName);
	ArrayList<Skill> findBySkillName(String skillName);
	boolean existsBySkillID(ObjectId skillID);
	Skill findBySkillID(ObjectId skillID);
	ArrayList<Skill> findBySkillLevel(int skillLevel);
}
