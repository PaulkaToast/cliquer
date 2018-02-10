
package com.styxxco.cliquer.Database;

public interface SkillRepository extends MongoRepository<Skill, String>
{
	public Skill findBySkillName(String skillName);
}
