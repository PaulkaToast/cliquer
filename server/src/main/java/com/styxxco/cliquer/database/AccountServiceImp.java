package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Message;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

public class AccountServiceImp implements AccountService
{
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImp.class);

    private final AccountRepository accountRepository;
    private final SkillRepository skillRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;

    private final GroupServiceImp groupService;

    public AccountServiceImp(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
        this.groupService = new GroupServiceImp(ar, sr, mr, gr);
    }

    @Override
    public Account createAccount( String username, String firstName, String lastName)
    {
        if(accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " already exists");
            return null;
        }
        Account user = new Account(username, firstName, lastName);
        this.accountRepository.save(user);
        return user;
    }

    @Override
    public Account getUserProfile(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        return user;
    }

    @Override
    public Account updateUserProfile(String username, String field, String value)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        switch(field)
        {
            case "firstName" : user.setFirstName(value); break;
            case "lastName" : user.setLastName(value); break;
            case "isPublic" : user.setPublic(Boolean.parseBoolean(value)); break;
            case "reputationReq" :
                double repReq;
                try
                {
                    repReq = Double.parseDouble(value);
                }
                catch(NumberFormatException e)
                {
                    logger.info("Invalid reputation requirement");
                    return null;
                }
                if(repReq < 0.0 || repReq > 1.0)
                {
                    logger.info("Invalid reputation requirement");
                    return null;
                }
                user.setReputationReq(repReq);
                break;
            case "proximityReq" :
                int proxReq;
                try
                {
                    proxReq = Integer.parseInt(value);
                }
                catch(NumberFormatException e)
                {
                    logger.info("Invalid proximity requirement");
                    return null;
                }
                if(proxReq <= 0)
                {
                    logger.info("Invalid proximity requirement");
                    return null;
                }
                user.setProximityReq(proxReq);
                break;
            default:
                logger.info("Field " + field + " is invalid");
                return null;
        }
        accountRepository.save(user);
        return user;
    }

    @Override
    public Account getMemberProfile(ObjectId accountID)
    {
        if(!accountRepository.existsByAccountID(accountID))
        {
            logger.info("User " + accountID + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(accountID);
        /* Mask private information and settings */
        user.setUsername(null);
        user.setModerator(false);
        user.setMessageIDs(null);
        user.setProximityReq(-1);
        user.setReputationReq(-1.0);
        return user;
    }

    @Override
    public Account getPublicProfile(ObjectId accountID)
    {
        if(!accountRepository.existsByAccountID(accountID))
        {
            logger.info("User " + accountID + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(accountID);
        if(!user.isPublic())
        {
            /* Mask all information except name and reputation */
            user.setSkillIDs(null);
            user.setGroupIDs(null);
            user.setFriendIDs(null);
        }
        /* Mask private information and settings */
        user.setUsername(null);
        user.setModerator(false);
        user.setMessageIDs(null);
        user.setProximityReq(-1);
        user.setReputationReq(-1.0);
        return user;
    }

    @Override
    public Skill addSkillToDatabase(String skillName)
    {
        if(skillRepository.existsBySkillName(skillName))
        {
            logger.info("Skill " + skillName + " is already in database");
            return null;
        }
        Skill skill = new Skill(skillName, 0);
        skillRepository.save(skill);
        return skill;
    }

    @Override
    public ArrayList<Skill> getAllValidSkills()
    {
        ArrayList<Skill> skills = skillRepository.findBySkillLevel(0);
        Collections.sort(skills);
        return skills;
    }

    @Override
    public ArrayList<Skill> getAllUserSkills(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        ArrayList<Skill> skills = new ArrayList<>();
        for(ObjectId skillID : user.getSkillIDs())
        {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        return skills;
    }

    @Override
    public Skill getSkill(String username, String skillName)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        ArrayList<Skill> skills = this.getAllUserSkills(username);
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
    public Account addSkill(String username, String skillName, int skillLevel)
    {
        if(!skillRepository.existsBySkillName(skillName))
        {
            logger.info("Skill " + skillName + " is invalid");
            return null;
        }
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        if(this.getSkill(username, skillName) != null)
        {
            logger.info("User " + username + " already has skill " + skillName);
            return null;
        }
        if(skillLevel < 0 || skillLevel > 10)
        {
            logger.info("Skill level " + skillLevel + " is invalid");
            return null;
        }
        Skill skill = new Skill(skillName, skillLevel);
        skillRepository.save(skill);
        user.addSkill(skill.getSkillID());
        accountRepository.save(user);
        return user;
    }

    @Override
    public Account removeSkill(String username, String skillName)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Skill skill = this.getSkill(username, skillName);
        if(skill == null)
        {
            logger.info("User " + username + " does not have skill " + skillName);
            return null;

        }
        skillRepository.delete(skill);
        user.removeSkill(skill.getSkillID());
        accountRepository.save(user);
        return user;
    }

    @Override
    public ArrayList<Message> getNewMessages(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        ArrayList<Message> messages = new ArrayList<>();
        for(ObjectId id : user.getMessageIDs())
        {
            Message message = messageRepository.findByMessageID(id);
            if(!message.isRead())
            {
                messages.add(message);
                message.setRead(true);
            }
        }
        return messages;
    }

    @Override
    public Message sendMessage(String username, ObjectId receiverID, String content, String type)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        if(!accountRepository.existsByAccountID(receiverID))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Account sender = accountRepository.findByUsername(username);
        Account receiver = accountRepository.findByAccountID(receiverID);
        Message message = new Message(sender.getAccountID(), content, type);
        messageRepository.save(message);
        receiver.addMessage(message.getMessageID());
        accountRepository.save(receiver);
        return message;
    }

    /* TODO: Sprint 2 */
    @Override
    public Account joinGroup(String username, ObjectId groupID)
    {
        return null;
    }

    @Override
    public Account leaveGroup(String username, ObjectId groupID)
    {
        if(!accountRepository.existsByUsername(username))
        {
            logger.info("User " + username + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupID))
        {
            logger.info("Group " + groupID + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Group group = groupRepository.findByGroupID(groupID);
        if(!groupService.hasGroupMember(group, user.getAccountID()))
        {
            logger.info("User " + username + " is not in group " + groupID);
            return null;
        }
        group.removeGroupMember(user.getAccountID());
        groupRepository.save(group);
        user.removeGroup(groupID);
        accountRepository.save(user);
        return user;

    }

}

