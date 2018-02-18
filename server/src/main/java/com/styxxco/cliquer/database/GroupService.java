package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Account;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public interface GroupService {
    /* Group Creation */
    Group createGroup(String groupName, String groupPurpose, ObjectId groupLeaderID);

    /* Group Access */
    Group getUserGroup(ObjectId groupID, ObjectId accountID);
    Group getPublicGroup(ObjectId groupID);

    /* Group Modification */
    Group updateGroupSettings(ObjectId groupID, ObjectId groupLeaderID, String field, String value);
    Group addGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID);
    Group removeGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID);
}