package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, Long> {
    Role findByAuthority(String authority);
}
