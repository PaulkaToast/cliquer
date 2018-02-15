package com.styxxco.cliquer.database;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Message;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class AccountServiceImp implements AccountService
{
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImp.class);

    private final AccountRepository accountRepository;
    private final SkillRepository skillRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;

    public AccountServiceImp(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
    }

    @Override
    public Account createAccount(String username, String firstName, String lastName)
    {
        Account check = accountRepository.findByUsername(username);
        if(check != null)
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
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        return user;
    }

    @Override
    public Account updateUserProfile(String username, String field, String value)
    {
        Account user = accountRepository.findByUsername(username);
        switch(field)
        {
            case "firstName" : user.setFirstName(value); break;
            case "lastName" : user.setLastName(value); break;
            case "isPublic" : user.setPublic(Boolean.parseBoolean(value)); break;
            case "reputationReq" :
                double repReq = Double.parseDouble(value);
                if(repReq <= 0.0 || repReq > 1.0)
                {
                    logger.info("Invalid reputation requirement");
                    return null;
                }
                user.setReputationReq(repReq);
                break;
            case "proximityReq" :
                int proxReq = Integer.parseInt(value);
                if(proxReq <= 0)
                {
                    logger.info("Invalid proximity requirement");
                }
                user.setProximityReq(proxReq); break;
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
        Account user = accountRepository.findByAccountID(accountID);
        if(user == null)
        {
            logger.info("User " + accountID + " not found");
            return null;
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
    public Account getPublicProfile(ObjectId accountID)
    {
        Account user = accountRepository.findByAccountID(accountID);
        if(user == null)
        {
            logger.info("User " + accountID + " not found");
            return null;
        }
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
    public ArrayList<Skill> getAllSkills(String username)
    {
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        ArrayList<Skill> skills = new ArrayList<>();
        for(ObjectId skillID : user.getSkillIDs())
        {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        return skills;
    }

    public Skill getSkill(String username, String skillName)
    {
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        ArrayList<Skill> skills = this.getAllSkills(username);
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
        ArrayList<Skill> check = skillRepository.findBySkillName(skillName);
        if(check.isEmpty())
        {
            logger.info("Skill " + skillName + " is invalid");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        if(this.getSkill(username, skillName) != null)
        {
            logger.info("User " + username + " already has skill " + skillName);
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
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        Skill skill = this.getSkill(username, skillName);
        if(this.getSkill(username, skillName) == null)
        {
            logger.info("User " + username + " does not have skill " + skillName);
            return null;

        }
        user.removeSkill(skill.getSkillID());
        return null;
    }

    @Override
    public ArrayList<Message> getNewMessages(String username)
    {
        Account user = accountRepository.findByUsername(username);
        if(user == null)
        {
            logger.info("User " + username + " not found");
            return null;
        }
        ArrayList<Message> messages = new ArrayList<>();
        for(ObjectId id : user.getMessageIDs())
        {
            Message message = messageRepository.findByMessageID(id);
            if(!message.isRead())
            {
                messages.add(message);
            }
        }
        return messages;
    }
}

