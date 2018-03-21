package com.styxxco.cliquer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Searchable {
    @JsonIgnore
    private final boolean isSearchable = true;
}
