package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.database.AccountRepository;
import com.styxxco.cliquer.database.GroupRepository;
import com.styxxco.cliquer.database.MessageRepository;
import com.styxxco.cliquer.database.SkillRepository;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Log4j
@Service(value = GroupServiceImpl.NAME)
public class GroupServiceImpl implements GroupService {
    public final static String NAME = "GroupService";

    private final AccountRepository accountRepository;
    private final SkillRepository skillRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;

    public GroupServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
    }

    @Override
    public Group createGroup(String groupName, String groupPurpose, ObjectId groupLeaderID)
    {
        if(!accountRepository.existsByAccountID(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " not found");
            return null;
        }
        Group group = new Group(groupName, groupPurpose, groupLeaderID);
        this.groupRepository.save(group);
        return group;
    }

    @Override
    public String deleteGroup(ObjectId groupID, ObjectId groupLeaderID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        for(ObjectId accountID : group.getGroupMemberIDs())
        {
            Account account = accountRepository.findByAccountID(accountID);
            account.removeGroup(groupID);
            accountRepository.save(account);
        }
        groupRepository.delete(group);
        return "Success";

    }

    @Override
    public Group getUserGroup(ObjectId groupID, ObjectId accountID)
    {
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        for(ObjectId id : group.getGroupMemberIDs())
        {
            if(id.equals(accountID))
            {
                return group;
            }
        }
        log.info("User " + accountID + " is not a member of group " + groupID);
        return null;
    }

    @Override
    public Group getPublicGroup(ObjectId groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.isPublic())
        {
            group.setGroupLeaderID(null);
            group.setGroupMemberIDs(null);
            group.setSkillReqs(null);
        }
        group.setProximityReq(-1);
        return group;
    }

    @Override
    public Group updateGroupSettings(ObjectId groupID, ObjectId groupLeaderID, String field, String value)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        switch(field)
        {
            case "groupName" : group.setGroupName(value); break;
            case "groupPurpose" : group.setGroupPurpose(value); break;
            case "groupLeaderID" : group.setGroupLeaderID(new ObjectId(value)); break;
            case "isPublic" : group.setPublic(Boolean.parseBoolean(value)); break;
            case "reputationReq" :
                double repReq;
                try
                {
                    repReq = Double.parseDouble(value);
                }
                catch(NumberFormatException e)
                {
                    log.info("Invalid reputation requirement");
                    return null;
                }
                if(repReq < 0.0 || repReq > 1.0)
                {
                    log.info("Invalid reputation requirement");
                    return null;
                }
                group.setReputationReq(repReq);
                break;
            case "proximityReq" :
                int proxReq;
                try
                {
                    proxReq = Integer.parseInt(value);
                }
                catch(NumberFormatException e)
                {
                    log.info("Invalid proximity requirement");
                    return null;
                }
                if(proxReq <= 0)
                {
                    log.info("Invalid proximity requirement");
                    return null;
                }
                group.setProximityReq(proxReq);
                break;
            default:
                log.info("Field " + field + " is invalid");
                return null;
        }
        groupRepository.save(group);
        return group;
    }

    @Override
    public boolean hasGroupMember(Group group, ObjectId accountID)
    {
        for(ObjectId id : group.getGroupMemberIDs())
        {
            if(id.equals(accountID))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Group addGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(this.hasGroupMember(group, accountID))
        {
            log.info("User " + accountID + " is already in group " + groupID);
            return null;
        }
        group.addGroupMember(accountID);
        groupRepository.save(group);
        Account member = accountRepository.findByAccountID(accountID);
        member.addGroup(groupID);
        accountRepository.save(member);
        return group;
    }

    @Override
    public Group removeGroupMember(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(!this.hasGroupMember(group, accountID))
        {
            log.info("User " + accountID + " is not in group " + groupID);
            return null;
        }
        group.removeGroupMember(accountID);
        groupRepository.save(group);
        Account member = accountRepository.findByAccountID(accountID);
        member.removeGroup(groupID);
        accountRepository.save(member);
        return group;
    }

    @Override
    public ArrayList<Skill> getAllSkillReqs(ObjectId groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        ArrayList<Skill> skills = new ArrayList<>();
        for(ObjectId skillID : group.getSkillReqs())
        {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        return skills;
    }

    @Override
    public Skill getSkillReq(ObjectId groupID, String skillName)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        ArrayList<Skill> skills = this.getAllSkillReqs(groupID);
        for(Skill skill : skills)
        {
            if(skill.getSkillName().equals(skillName))
            {
                return skill;
            }
        }
        return null;
    }

    @Override
    public Group addSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName, int skillLevel)
    {
        if(!skillRepository.existsBySkillName(skillName))
        {
            log.info("Skill " + skillName + " is invalid");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        if(this.getSkillReq(groupID, skillName) != null)
        {
            log.info("Group " + groupID + " already has skill requirement " + skillName);
            return null;
        }
        if(skillLevel < 0 || skillLevel > 10)
        {
            log.info("Skill level " + skillLevel + " is invalid");
            return null;
        }
        Skill skill = new Skill(skillName, skillLevel);
        skillRepository.save(skill);
        group.addSkillReq(skill.getSkillID());
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group removeSkillReq(ObjectId groupID, ObjectId groupLeaderID, String skillName)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " is not the leader of group " + groupID);
            return null;
        }
        Skill skill = this.getSkillReq(groupID, skillName);
        if(skill == null)
        {
            log.info("Group " + groupID + " does not have skill requirement " + skillName);
            return null;
        }
        skillRepository.delete(skill);
        group.removeSkillReq(skill.getSkillID());
        groupRepository.save(group);
        return group;
    }

    @Override
    public Message sendMessage(ObjectId groupID, ObjectId senderID, ObjectId receiverID, String content, String type)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.getGroupLeaderID().equals(senderID))
        {
            log.info("User " + senderID + " is not the leader of group " + groupID);
            return null;
        }
        Account receiver = accountRepository.findByAccountID(receiverID);
        Message message = new Message(groupID, content, type);
        messageRepository.save(message);
        receiver.addMessage(message.getMessageID());
        accountRepository.save(receiver);
        return message;
    }
}