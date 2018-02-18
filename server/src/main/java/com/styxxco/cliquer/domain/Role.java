package com.styxxco.cliquer.domain;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = -8186644851823152209L;

    @Id
    @Generated
    private Long id;

    private String authority;

    public Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String toString() {
        return authority;
    }

}
