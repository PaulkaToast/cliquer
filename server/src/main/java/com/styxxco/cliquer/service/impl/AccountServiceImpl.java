package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.SecurityConfiguration;
import com.styxxco.cliquer.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service(value = AccountServiceImpl.NAME)
public class AccountServiceImpl implements AccountService
{
    public final static String NAME = "AccountService";

    private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GroupRepository groupRepoitory;

    public AccountServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepoitory = gr;
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
    @Transactional
    @Secured(value = SecurityConfiguration.Roles.ROLE_ANONYMOUS)
    public Account registerUser(RegisterUser init) {

        Account userLoaded = accountRepository.findByUsername(init.getUserName());

        if (userLoaded == null) {
            Account account = new Account(init.getUserName(), init.getEmail());
            account.setAuthorities(getUserRoles());
            account.setPassword(UUID.randomUUID().toString());
            System.out.println(account.toString());
            accountRepository.save(account);
            logger.info("registerUser -> user created");
            return account;
        } else {
            logger.info("registerUser -> user exists");
            return userLoaded;
        }
    }

    @PostConstruct
    public void init() {
        if (accountRepository.count() == 0) {
                Account account = new Account("mod", "mod", "test@gmail.com");
                account.setAuthorities(getModRoles());
                accountRepository.save(account);
        }
    }

    private List<Role> getModRoles() {
        return Collections.singletonList(getRole(SecurityConfiguration.Roles.ROLE_MOD));
    }

    private List<Role> getUserRoles() {
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
    public Account getMemberProfile(String accountID)
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
    public Account getPublicProfile(String accountID)
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
            user.setSkills(null);
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
    public Account addSkill(String username, String skillName, int skillLevel)
    {
        Skill check = skillRepository.findBySkillName(skillName);
        if(check == null)
        {
            logger.info("Skill " + skillName + " is invalid");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        check = user.getSkill(skillName);
        if(check != null)
        {
            logger.info("User " + username + " already has skill " + skillName);
            return null;
        }
        Skill skill = new Skill(skillName, skillLevel);
        user.addSkill(skill);
        accountRepository.save(user);
        return user;
    }

    @Override
    public Account removeSkill(String username, String skillName)
    {
        Account user = accountRepository.findByUsername(username);
        Skill skill = user.getSkill(skillName);
        if(skill == null)
        {
            logger.info("User " + username + " does not have skill " + skillName);
            return null;
        }
        user.removeSkill(skillName);
        accountRepository.save(user);
        return user;
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
        for(String id : user.getMessageIDs())
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

