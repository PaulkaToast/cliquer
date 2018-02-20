package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.SecurityConfiguration;
import com.styxxco.cliquer.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j
@Service(value = AccountServiceImpl.NAME)
public class AccountServiceImpl implements AccountService
{
    public final static String NAME = "AccountService";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    public AccountServiceImpl() {

    }

    public AccountServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
        this.groupService = new GroupServiceImpl(ar, sr, mr, gr);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = accountRepository.findByUsername(username);
        if (userDetails == null)
            return null;

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (GrantedAuthority role: userDetails.getAuthorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public Account createAccount(String username, String firstName, String lastName)
    {
        if(accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " already exists");
            return null;
        }
        Account user = new Account(username, firstName, lastName);
        this.accountRepository.save(user);
        return user;
    }

    @Override
    public String deleteAccount(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        for(ObjectId groupID : user.getGroupIDs())
        {
            Group group = groupRepository.findByGroupID(groupID);
            group.removeGroupMember(user.getAccountID());
            if(group.getGroupLeaderID().equals(user.getAccountID()))
            {
                group.setGroupLeaderID(group.getGroupMemberIDs().get(0));
            }
            groupRepository.save(group);
        }
        accountRepository.delete(user);
        return "Success";
    }

    @Override
    @Transactional
    public Account registerUser(RegisterUser init) {

        Account userLoaded = accountRepository.findByUsername(init.getUserName());

        if (userLoaded == null) {
            Account account = new Account(init.getUserName(), init.getEmail());
            account.setAuthorities(getUserRoles());
            account.setPassword(UUID.randomUUID().toString());
            System.out.println(account.toString());
            accountRepository.save(account);
            log.info("registerUser -> user created");
            return account;
        } else {
            log.info("registerUser -> user exists");
            return userLoaded;
        }
    }

    @Override
    public List<Role> getModRoles() {
        return Collections.singletonList(getRole(SecurityConfiguration.Roles.ROLE_MOD));
    }

    @Override
    public List<Role> getUserRoles() {
        return Collections.singletonList(getRole(SecurityConfiguration.Roles.ROLE_USER));
    }

    @Override
    public List<Role> getAnonRoles() {
        return Collections.singletonList(getRole(SecurityConfiguration.Roles.ROLE_ANONYMOUS));
    }

    private Role getRole(String authority) {
        Role modRole = roleRepository.findByAuthority(authority);
        if (modRole == null) {
            return new Role(authority);
        } else {
            return modRole;
        }
    }

    @Override
    public Account getUserProfile(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
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
            log.info("User " + username + " not found");
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
                    log.info("Invalid reputation requirement");
                    return null;
                }
                if(repReq < 0.0 || repReq > 1.0)
                {
                    log.info("Invalid reputation requirement");
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
                    log.info("Invalid proximity requirement");
                    return null;
                }
                if(proxReq <= 0)
                {
                    log.info("Invalid proximity requirement");
                    return null;
                }
                user.setProximityReq(proxReq);
                break;
            default:
                log.info("Field " + field + " is invalid");
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
            log.info("User " + accountID + " not found");
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
    public Account maskPublicProfile(Account account)
    {
        if(!account.isPublic())
        {
            /* Mask all information except name and reputation */
            account.setSkillIDs(null);
            account.setGroupIDs(null);
            account.setFriendIDs(null);
        }
        /* Mask private information and settings */
        account.setUsername(null);
        account.setModerator(false);
        account.setMessageIDs(null);
        account.setProximityReq(-1);
        account.setReputationReq(-1.0);
        return account;
    }

    @Override
    public Account getPublicProfile(ObjectId accountID)
    {
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(accountID);
        return this.maskPublicProfile(user);
    }

    @Override
    public ArrayList<Account> searchByFirstName(String firstName)
    {
        ArrayList<Account> accounts = accountRepository.findByFirstName(firstName);
        ArrayList<Account> masked = new ArrayList<>();
        for(Account account : accounts)
        {
            masked.add(this.maskPublicProfile(account));
        }
        return masked;
    }

    @Override
    public ArrayList<Account> searchByLastName(String lastName)
    {
        ArrayList<Account> accounts = accountRepository.findByLastName(lastName);
        ArrayList<Account> masked = new ArrayList<>();
        for(Account account : accounts)
        {
            masked.add(this.maskPublicProfile(account));
        }
        return masked;
    }

    @Override
    public ArrayList<Account> searchByReputation(int minimumRep)
    {
        List<Account> accounts = accountRepository.findAll();
        ArrayList<Account> qualified = new ArrayList<>();
        for(Account account : accounts)
        {
            if(account.getReputation() >= minimumRep)
            {
                qualified.add(this.maskPublicProfile(account));
            }
        }
        return qualified;
    }

    /* TODO Decide if private accounts should come up, since skills are private information */
    @Override
    public ArrayList<Account> searchBySkill(String skillName, int minimumLevel)
    {
        List<Account> accounts = accountRepository.findAll();
        ArrayList<Account> qualified = new ArrayList<>();
        for(Account account : accounts)
        {
            Skill skill = this.getSkill(account.getUsername(), skillName);
            if(skill != null && skill.getSkillLevel() >= minimumLevel)
            {
                qualified.add(this.maskPublicProfile(account));
            }
        }
        return qualified;
    }

    @Override
    public Skill addSkillToDatabase(String skillName)
    {
        if(skillRepository.existsBySkillName(skillName))
        {
            log.info("Skill " + skillName + " is already in database");
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
            log.info("User " + username + " not found");
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
            log.info("User " + username + " not found");
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
            log.info("Skill " + skillName + " is invalid");
            return null;
        }
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        if(this.getSkill(username, skillName) != null)
        {
            log.info("User " + username + " already has skill " + skillName);
            return null;
        }
        if(skillLevel < 0 || skillLevel > 10)
        {
            log.info("Skill level " + skillLevel + " is invalid");
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
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Skill skill = this.getSkill(username, skillName);
        if(skill == null)
        {
            log.info("User " + username + " does not have skill " + skillName);
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
            log.info("User " + username + " not found");
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
                messageRepository.save(message);
            }
        }
        return messages;
    }

    @Override
    public Message sendMessage(String username, ObjectId receiverID, String content, String type)
    {
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
            return null;
        }
        if(!accountRepository.existsByAccountID(receiverID))
        {
            log.info("User " + username + " not found");
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
            log.info("User " + username + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Group group = groupRepository.findByGroupID(groupID);
        if(!groupService.hasGroupMember(group, user.getAccountID()))
        {
            log.info("User " + username + " is not in group " + groupID);
            return null;
        }
        if(group.getGroupLeaderID().equals(user.getAccountID()))
        {
            if(group.getGroupMemberIDs().size() == 1)
            {
                groupRepository.delete(group);
                user.removeGroup(groupID);
                accountRepository.save(user);
                return user;
            }
            else
            {
                group.setGroupLeaderID(group.getGroupMemberIDs().get(0));
            }

        }
        group.removeGroupMember(user.getAccountID());
        groupRepository.save(group);
        user.removeGroup(groupID);
        accountRepository.save(user);
        return user;

    }

    public double getReputationRanking(String username)
    {
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
            return 0.0;
        }
        Account user = accountRepository.findByUsername(username);
        List<Account> accounts = accountRepository.findAll();
        ArrayList<Integer> reputations = new ArrayList<>();
        for(Account account : accounts)
        {
            reputations.add(account.getReputation());
        }
        Collections.sort(reputations);
        int rank = reputations.lastIndexOf(user.getReputation()) + 1;
        double percentile = (100.0*rank)/reputations.size();
        return percentile;
    }

}

