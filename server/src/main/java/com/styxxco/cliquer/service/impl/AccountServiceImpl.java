package com.styxxco.cliquer.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Message.Types;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.security.SecurityConfiguration;
import com.styxxco.cliquer.service.AccountService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Service(value = AccountServiceImpl.NAME)
public class AccountServiceImpl implements AccountService {
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

    @Autowired
    private SimpMessagingTemplate template;

    @Value("${spring.account.path}")
    private String picturePath;

    public AccountServiceImpl() {

    }

    public AccountServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr, RoleRepository rr) {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
        this.roleRepository = rr;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = accountRepository.findByUsername(username);
        if (userDetails == null)
            return null;

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (GrantedAuthority role : userDetails.getAuthorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        return new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public Account createAccount(String username, String email, String firstName, String lastName) {
        if (accountRepository.existsByUsername(username)) {
            log.info("User " + username + " already exists");
            return null;
        }
        Account user = new Account(username, email, firstName, lastName);
        user.log("Account created");
        this.accountRepository.save(user);
        return user;
    }

    @Override
    public Account deleteAccount(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        for (String groupID : user.getGroupIDs().keySet()) {
            Group group = groupRepository.findByGroupID(groupID);
            group.removeGroupMember(user.getAccountID());
            if (group.getGroupLeaderID().equals(user.getAccountID())) {
                List<String> memberIDs = new ArrayList<>(group.getGroupMemberIDs().keySet());
                group.setGroupLeaderID(memberIDs.get(0));
                group.setGroupLeaderName(group.getGroupMemberIDs().get(memberIDs.get(0)));
            }
            groupRepository.save(group);
        }
        accountRepository.delete(user);
        return user;
    }

    @Override
    @Transactional
    public Account registerUser(FirebaseTokenHolder tokenHolder, String firstName, String lastName) {

        Account userLoaded = accountRepository.findByUsername(tokenHolder.getUid());

        if (userLoaded == null) {
            Account account = createAccount(tokenHolder.getUid(), tokenHolder.getEmail(), firstName, lastName);
            account.setAuthorities(getUserRoles());
            account.setPassword(UUID.randomUUID().toString());
            System.out.println(account.toString());
            accountRepository.save(account);
            log.info("registerUser -> user \"" + account.getFirstName() + " " + account.getLastName() + "\" created");
            return account;
        } else {
            log.info("registerUser -> user \"" + tokenHolder.getUid() + "\" exists");
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
    public Account getProfile(String userId) {
        Account user = accountRepository.findByAccountID(userId);
        if (user == null) {
            log.info("User " + userId + " not found");
            return null;
        }
        return user;
    }

    @Override
    public Account getProfile(String username, String userid, String type) {
        Account user = accountRepository.findByUsername(username);
        if (username != null) {
            user.setRank(this.getReputationRanking(user.getUsername()));
            switch (type) {
                case "user":
                    user = getUserProfile(username);
                    break;
                case "member":
                    user = getMemberProfile(username);
                    break;
                case "public":
                    user = getPublicProfile(username);
                    break;
            }
        } else {
            Account account = accountRepository.findByAccountID(userid);
            if (account != null) {
                account.setRank(this.getReputationRanking(account.getUsername()));
                String name = account.getUsername();
                switch (type) {
                    case "user":
                        user = getUserProfile(name);
                        break;
                    case "member":
                        user = getMemberProfile(name);
                        break;
                    case "public":
                        user = getPublicProfile(name);
                        break;
                }
            }
        }
        if (!user.isAccountEnabled()) {
            user.tryUnsuspend();
        }
        return user;
    }

    @Override
    public Account getUserProfile(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        user.setTimer();
        accountRepository.save(user);
        return user;
    }

    @Override
    public Map<String, ? extends Searchable> searchWithFilter(String type, String query, boolean suggestions, boolean weights) {
        switch (type) {
            case "firstname":
                return searchByFirstName(query).stream().collect(Collectors.toMap(Account::getUsername, _it -> _it));
            case "lastname":
                return searchByLastName(query).stream().collect(Collectors.toMap(Account::getUsername, _it -> _it));
            case "fullname":
                return searchByFullName(query).stream().collect(Collectors.toMap(Account::getUsername, _it -> _it));
            case "username":
                Map<String, Account> map = new HashMap<>();
                Account account = searchByUsername(query);
                map.put(account.getUsername(), account);
                return map;
            case "reputation":
                return searchByReputation(Integer.parseInt(query), suggestions, weights).stream().collect(Collectors.toMap(Account::getUsername, _it -> _it));
            case "skill":
                return searchBySkill(query).stream().collect(Collectors.toMap(Account::getUsername, _it -> _it));
            case "group":
                return searchByGroupName(query).stream().collect(Collectors.toMap(Group::getGroupID, _it -> _it));
            case "isPublic":
                return searchByGroupPublic(query).stream().collect(Collectors.toMap(Group::getGroupID, _it -> _it));
        }
        return null;
    }

    @Override
    public Account getMemberProfile(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        /* Mask private information and settings */
        user.setModerator(false);
        user.setMessageIDs(null);
        user.setPassword(null);
        user.setOptedOut(false);
        user.setProximityReq(-1);
        user.setReputationReq(-1.0);
        return user;
    }

    @Override
    public Account maskPublicProfile(Account account) {
        if (!account.isPublic()) {
            /* Mask all information except name and reputation */
            account.setSkillIDs(null);
            account.setGroupIDs(null);
            account.setFriendIDs(null);
        }
        /* Mask private information and settings */
        account.setModerator(false);
        account.setMessageIDs(null);
        account.setPassword(null);
        account.setOptedOut(false);
        account.setEmail(null);
        account.setLoggedInTime(-1);
        account.setProximityReq(-1);
        account.setReputationReq(-1.0);
        return account;
    }

    @Override
    public Account getPublicProfile(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        return this.maskPublicProfile(user);
    }

    @Override
    public List<Account> searchByFirstName(String firstName) {
        List<Account> accounts = accountRepository.findByFirstNameContainsIgnoreCase(firstName);
        List<Account> masked = new ArrayList<>();
        for (Account account : accounts) {
            if (!account.isOptedOut()) {
                masked.add(this.maskPublicProfile(account));
            }
        }
        Comparator<Account> byLastName = Comparator.comparing(Account::getLastName);
        masked.sort(byLastName);
        return masked;
    }

    @Override
    public List<Account> searchByLastName(String lastName) {
        List<Account> accounts = accountRepository.findByLastNameContainsIgnoreCase(lastName);
        List<Account> masked = new ArrayList<>();
        for (Account account : accounts) {
            if (!account.isOptedOut()) {
                masked.add(this.maskPublicProfile(account));
            }
        }
        Comparator<Account> byFirstName = Comparator.comparing(Account::getFirstName);
        masked.sort(byFirstName);
        return masked;
    }

    @Override
    public List<Account> searchByFullName(String firstName, String lastName) {
        List<Account> accounts = accountRepository.findByFirstNameContainsIgnoreCase(firstName);
        List<Account> masked = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getLastName().toLowerCase().equals(lastName.toLowerCase()) && !account.isOptedOut()) {
                masked.add(this.maskPublicProfile(account));
            }
        }
        return masked;
    }

    @Override
    public List<Account> searchByFullName(String fullName) {
        String arr[] = fullName.split(" ");
        if (arr.length == 2) {
            return searchByFullName(arr[0], arr[1]);
        }
        return null;
    }


    @Override
    public List<Account> searchByReputation(int minimumRep, boolean includeSuggested, boolean includeWeights) {
        List<Account> accounts = accountRepository.findAll();
        List<Account> qualified = new ArrayList<>();
        for (Account account : accounts) {
            int accountReputation;
            if (includeWeights) {
                accountReputation = account.getAdjustedReputation();
            } else {
                accountReputation = account.getReputation();
            }
            if (accountReputation >= minimumRep && !account.isOptedOut() &&
                    account.getReputation() * account.getReputationReq() <= minimumRep) {
                qualified.add(this.maskPublicProfile(account));
            }
        }
        Comparator<Account> byFirstName = Comparator.comparing(Account::getFirstName);
        qualified.sort(byFirstName);
        Comparator<Account> byLastName = Comparator.comparing(Account::getLastName);
        qualified.sort(byLastName);
        Comparator<Account> byReputation = Comparator.comparingInt(Account::getReputation);
        byReputation = byReputation.reversed();
        qualified.sort(byReputation);
        if (includeSuggested) {
            return this.moveSuggestedToTop(qualified, minimumRep, includeWeights);
        }
        return qualified;
    }

    @Override
    public List<Account> moveSuggestedToTop(List<Account> accounts, int reputation, boolean includeWeights) {
        if (accounts.size() < 5) {
            return accounts;
        }
        int suggestions = Math.min(5, accounts.size() / 5);
        List<Account> suggested = new ArrayList<>();
        for (Account account : accounts) {
            int accountReputation;
            if (includeWeights) {
                accountReputation = account.getAdjustedReputation();
            } else {
                accountReputation = account.getReputation();
            }
            if (accountReputation >= reputation && accountReputation <= reputation + Account.MAX_REP / 10) {
                suggested.add(account);
            }
        }
        List<Account> results = new ArrayList<>();
        for (int i = 0; i < suggestions && suggested.size() > 0; i++) {
            Account random = suggested.get((int) (Math.random() * suggested.size()));
            suggested.remove(random);
            accounts.remove(random);
            results.add(random);
        }
        Comparator<Account> byFirstName = Comparator.comparing(Account::getFirstName);
        results.sort(byFirstName);
        Comparator<Account> byLastName = Comparator.comparing(Account::getLastName);
        results.sort(byLastName);
        Comparator<Account> byReputation = Comparator.comparingInt(Account::getReputation);
        byReputation = byReputation.reversed();
        results.sort(byReputation);
        results.add(null);
        results.addAll(accounts);
        return results;
    }

    @Override
    public List<Account> searchBySkill(String skillName) {
        List<Account> accounts = accountRepository.findAll();
        Comparator<Account> byFirstName = Comparator.comparing(Account::getFirstName);
        accounts.sort(byFirstName);
        Comparator<Account> byLastName = Comparator.comparing(Account::getLastName);
        accounts.sort(byLastName);

        List<Account> qualified = new ArrayList<>();
        for (Account account : accounts) {
            for (int i = 10; i >= 1; i--) {
                Skill skill = this.getSkill(account.getUsername(), skillName);
                if (account.isPublic() && skill != null && skill.getSkillLevel() == i) {
                    qualified.add(this.maskPublicProfile(account));
                }
            }
        }
        return qualified;
    }

    @Override
    public Account searchByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public List<Group> searchByGroupName(String name) {
        List<Group> groups = groupRepository.findAllByGroupNameContainsIgnoreCase(name);
        List<Group> qualified = new ArrayList<>();
        for (Group group : groups) {
            if (group.isPublic()) {
                group.setGroupMemberIDs(null);
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByGroupPublic(String userId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        List<Group> qualified = groupService.searchBySettings(user.getUsername(), groupRepository.findAll());
        List<Group> groups = new ArrayList<>();
        for (Group group : qualified) {
            if (group.isPublic()) {
                group.setGroupMemberIDs(null);
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    public List<Skill> addSkills(String username, String json) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        List<Skill> skills = new ArrayList<>();
        try {
            ObjectMapper om = new ObjectMapper();
            TypeFactory typeFactory = om.getTypeFactory();
            List<String> list = om.readValue(json, typeFactory.constructCollectionType(List.class, String.class));
            for (String s : list) {
                skills.add(addSkill(user, s, "0"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skills;
    }

    @Override
    public Skill addSkillToDatabase(String skillName) {
        for (int i = 0; i <= 10; i++) {
            if (!skillRepository.existsBySkillNameAndSkillLevel(skillName, i)) {
                Skill skill = new Skill(skillName, i);
                skillRepository.save(skill);
            }
        }
        return skillRepository.findBySkillNameAndSkillLevel(skillName, 0);
    }

    @Override
    public List<Skill> getAllValidSkills() {
        List<Skill> skills = skillRepository.findBySkillLevel(0);
        System.out.println(skills);
        Collections.sort(skills);
        return skills;
    }

    @Override
    public List<Skill> getAllUserSkills(String userId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        List<Skill> skills = new ArrayList<>();
        for (String skillID : user.getSkillIDs().keySet()) {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        return skills;
    }

    @Override
    public List<Group> getAllUserGroups(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        List<Group> groups = new ArrayList<>();
        for (String groupID : user.getGroupIDs().keySet()) {
            Group group = groupRepository.findByGroupID(groupID);
            groups.add(group);
        }
        return groups;
    }

    @Override
    public Skill getSkill(String username, String skillName) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        List<Skill> skills = this.getAllUserSkills(user.getAccountID());
        for (Skill skill : skills) {
            if (skill.getSkillName().equals(skillName)) {
                return skill;
            }
        }
        return null;
    }

    @Override
    public Skill addSkill(String username, String skillName, String skillString) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        return addSkill(user, skillName, skillString);
    }

    private Skill addSkill(Account account, String skillName, String skillString) {
        if (!skillRepository.existsBySkillName(skillName)) {
            log.info("Skill " + skillName + " is invalid");
            return null;
        }
        int skillLevel = 0;
        try {
            skillLevel = Integer.parseInt(skillString);
        } catch (NumberFormatException e) {
            log.info("Could not parse " + skillString + " as an integer");
        }
        if (skillLevel < 0 || skillLevel > 10) {
            log.info("Skill level " + skillLevel + " is invalid");
            return null;
        }

        Skill skill;
        if (skillRepository.existsBySkillNameAndSkillLevel(skillName, skillLevel)) {
            skill = skillRepository.findBySkillNameAndSkillLevel(skillName, skillLevel);
        } else if (skillRepository.existsBySkillName(skillName)) {
            skill = new Skill(skillName, skillLevel);
            skillRepository.save(skill);
        } else {
            return null;
        }
        Skill curr = this.getSkill(account.getUsername(), skillName);
        if (curr != null) {
            if (curr.getSkillLevel() == skillLevel) {
                log.info("User " + account.getUsername() + " already has skill " + skillName);
                return null;
            } else {
                account.removeSkill(curr.getSkillID());
            }
        }
        account.addSkill(skill);
        account.log("Added skill " + skill.getSkillName());
        accountRepository.save(account);
        return skill;
    }

    @Override
    public Account removeSkill(String username, String skillName) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Skill skill = this.getSkill(username, skillName);
        if (skill == null) {
            log.info("User " + username + " does not have skill " + skillName);
            return null;

        }
        user.removeSkill(skill.getSkillID());
        user.log("Remove skill " + skill.getSkillName());
        accountRepository.save(user);
        return user;
    }

    @Override
    public List<Message> getMessages(String userId, String read, String startDate /* YEAR-MONTH-DAY */) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        List<Message> messages = new ArrayList<>();
        LocalDate start = null;
        if(startDate != null) {
            try {
                start = LocalDate.parse(startDate);
            } catch (DateTimeParseException e) {
                log.info("Date " + startDate + " is invalid");
                return null;
            }
        }

        boolean includeRead = false;
        try {
            includeRead = Boolean.parseBoolean(read);
        } catch (Exception e) {
            log.info("Could not parse boolean");
        }

        for (String id : user.getMessageIDs().keySet()) {
            Message message = messageRepository.findByMessageID(id);
            if ((includeRead || !message.isRead()) && (start == null || !message.getCreationDate().isBefore(start))) {
                messages.add(message);
            }
        }
        Comparator<Message> byTime = Comparator.comparing(Message::getCreationTime);
        messages.sort(byTime);
        Comparator<Message> byDate = Comparator.comparing(Message::getCreationDate);
        messages.sort(byDate);
        Map<String, Message> map = messages.stream().collect(Collectors.toMap(Message::getMessageID, _it -> _it));
        try {
            template.convertAndSend("/notification/" + userId, map);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return messages;
    }

    @Override
    public Message rejectModInvite(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        messageRepository.delete(messageId);
        user.removeMessage(messageId);
        user.deniedMod();
        user.log("Reject mod invite");
        accountRepository.save(user);
        return message;
    }

    @Override
    public Message rejectModRequest(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        messageRepository.delete(messageId);
        user.removeMessage(messageId);
        accountRepository.save(user);
        Message parent = messageRepository.findByParentIDAndAndSenderID(message.getParentID());
        parent.decrement();
        messageRepository.save(parent);

        if (parent.getCounter() < -3) {
            deleteMessageByParent(parent.getParentID());
            user.deniedMod();
            user.log("Deny mod request");
            accountRepository.save(user);
        }
        return message;
    }

    @Override
    public Message acceptModInvite(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        messageRepository.delete(messageId);
        user.removeMessage(messageId);
        user.log("Send mod request");
        accountRepository.save(user);
        String parentID = "modRequest[" + userId + "]";
        Message request = new Message(parentID, user.getFullName(), "I would like to become a moderator. Care to look at my account?", Types.MOD_REQUEST);
        request.setParentID(parentID);
        sendMessageToMods(userId, request);
        return request;
    }

    @Override
    public Message acceptModRequest(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        messageRepository.delete(messageId);
        user.removeMessage(messageId);
        accountRepository.save(user);
        Message parent = messageRepository.findByParentIDAndAndSenderID(message.getParentID());
        parent.increment();
        messageRepository.save(parent);

        // TODO: update for decided rules
        if (parent.getCounter() > 5) {
            deleteMessageByParent(parent.getParentID());
            addToModerators(userId);
            user.log("Accept mod request");
            accountRepository.save(user);
        }
        return message;
    }

    @Override
    public void handleRejectNotification(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return;
        }
        Message message = messageRepository.findByMessageID(messageId);

        switch (message.getType()) {

            case Types.GROUP_INVITE:
            case Types.FRIEND_INVITE:
            case Types.SEARCH_INVITE:
                rejectInvite(userId, messageId);
                break;
            case Types.JOIN_REQUEST:
                rejectJoinRequest(userId, messageId);
                break;
            case Types.MOD_INVITE:
                rejectModInvite(userId, messageId);
                break;
            case Types.MOD_REQUEST:
                rejectModRequest(userId, messageId);
                break;
        }
    }

    @Override
    public void handleAcceptNotification(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return;
        }
        Message message = messageRepository.findByMessageID(messageId);

        switch (message.getType()) {

            case Types.GROUP_INVITE:
                acceptGroupInvite(userId, messageId);
                break;
            case Types.FRIEND_INVITE:
                acceptFriendInvite(userId, messageId);
                break;
            case Types.JOIN_REQUEST:
                acceptJoinRequest(userId, messageId);
                break;
            case Types.MOD_INVITE:
                acceptModInvite(userId, messageId);
                break;
            case Types.MOD_REQUEST:
                acceptModRequest(userId, messageId);
                break;
            case Types.SEARCH_INVITE:
                acceptSearchInvite(userId, messageId);
        }
    }

    @Override
    public List<Message> getChatHistory(String groupId, String userId) {
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.hasGroupMember(userId)) {
            log.info("User " + userId + " is not in group " + groupId);
            return null;
        }
        List<Message> messages = new ArrayList<>();
        for(String messageID : group.getChatMessageIDs()) {
            Message message = messageRepository.findByMessageID(messageID);
            messages.add(message);
        }
        try {
            template.convertAndSend("/group/" + userId + "/" + groupId, messages);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return messages;
    }

    @Override
    public Message reactToChatMessage(String groupId, String userId, String messageId, int reaction)
    {
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.hasGroupMember(userId)) {
            log.info("User " + userId + " is not in group " + groupId);
            return null;
        }
        if(!group.hasMessage(messageId)) {
            log.info("Message " + messageId + " not found in chat for group " + groupId);
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        if(message.getReaction(userId) == reaction) {
            message.removeReaction(userId);
        } else {
            message.addReaction(userId, reaction);
        }
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message sendMessage(String senderId, String receiverId, String content, int type) {
        String senderName;
        if (accountRepository.existsByAccountID(senderId)) {
            senderName = accountRepository.findByAccountID(senderId).getFullName();
        } else if (groupRepository.existsByGroupID(senderId)) {
            senderName = groupRepository.findByGroupID(senderId).getGroupName();
        } else {
            log.info("Sender " + senderId + " not found");
            return null;
        }
        Message message;
        if(type == Types.CHAT_MESSAGE) {
            if (!groupRepository.existsByGroupID(receiverId)) {
                log.info("Group " + receiverId + " not found");
                return null;
            }
            Group receiver = groupRepository.findByGroupID(receiverId);
            message = new Message(senderId, senderName, content, type);
            messageRepository.save(message);
            receiver.addMessage(message.getMessageID());
            groupRepository.save(receiver);
        } else {
            if (!accountRepository.existsByAccountID(receiverId)) {
                log.info("Account " + receiverId + " not found");
                return null;
            }
            Account receiver = accountRepository.findByAccountID(receiverId);
            message = new Message(senderId, senderName, content, type);
            messageRepository.save(message);
            receiver.addMessage(message);
            accountRepository.save(receiver);
        }
        return message;
    }

    @Override
    public Message sendMessageToMods(String senderId, Message message) {
        List<Account> mods = accountRepository.findByIsModeratorTrue();
        for (Account mod : mods) {
            Message copy = new Message(message.getSenderID(), message.getSenderName(), message.getContent(), message.getType());
            copy.setParentID(message.getParentID());
            copy.setGroupID(message.getGroupID());
            copy.setTopicID(message.getTopicID());
            copy.setChatMessageID(message.getChatMessageID());
            mod.addMessage(copy);
            messageRepository.save(copy);
            accountRepository.save(mod);
        }
        try {
            template.convertAndSend("/moderators", message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Map<String, Integer> getRateForm(String userId, String rateeId, String groupId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if ((!groupRepository.existsByGroupID(groupId))) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if (!group.getGroupMemberIDs().keySet().contains(user.getAccountID())) {
            log.info("User " + user.getFullName() + " is not in group " + group.getGroupName());
            return null;
        }
        return groupService.getGroupMemberRatingForm(groupId, rateeId);
    }

    @Override
    public Message deleteMessage(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(messageId)) {
            log.info("User " + userId + " did not receive message " + messageId);
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        user.removeMessage(message.getMessageID());
        accountRepository.save(user);
        messageRepository.delete(message.getMessageID());
        return message;
    }

    /* Deletes all messages associated with said parent NOTE: ONLY SEARCHES THROUGH MODS */
    @Override
    public void deleteMessageByParent(String parentId) {
        List<Message> group = messageRepository.findByParentID(parentId);
        List<Account> mods = accountRepository.findByIsModeratorTrue();
        for (Account mod : mods) {
            for (Message message : group) {
                mod.removeMessage(message.getMessageID());
            }
            accountRepository.save(mod);
        }
        messageRepository.delete(group);
    }

    @Override
    public Message readMessage(String userId, String messageId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(messageId)) {
            log.info("User " + userId + " did not receive message " + messageId);
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        message.setRead(true);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Group createGroup(String username, String json) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Group group = null;
        try {
            JSONObject obj = new JSONObject(json);
            String name = obj.getString("groupName");
            String purpose = obj.getString("groupPurpose");
            group = groupService.createGroup(name, purpose, user.getAccountID());
            for (Object k : obj.keySet()) {
                String key = k.toString();
                if (key.contentEquals("skillsReq")) {
                    JSONArray skills = obj.getJSONArray("skillsReq");
                    for (int i = 0; i < skills.length(); i++) {
                        if (skillRepository.existsBySkillName(skills.getString(i))) {
                            Skill skill = skillRepository.findBySkillNameAndSkillLevel(skills.getString(i), 0);
                            group.addSkillReq(skill);
                        }
                    }
                    groupRepository.save(group);
                }
                groupService.updateGroupSettings(group.getGroupID(), group.getGroupLeaderID(), key, obj.get(key).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (group != null) {
            user.log("Create group " + group.getGroupName());
            accountRepository.save(user);
        }
        return group;
    }

    @Override
    public Group createEvent(String groupId, String json) {
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        String purpose = null;
        int proximity = -1;
        List<String> skillNames = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(json);
            purpose = obj.getString("purpose");
            proximity = obj.getInt("proximity");
            for (Object k : obj.keySet()) {
                String key = k.toString();
                if (key.contentEquals("skillsReq")) {
                    JSONArray skills = obj.getJSONArray("skillsReq");
                    for (int i = 0; i < skills.length(); i++) {
                        if (skillRepository.existsBySkillName(skills.getString(i))) {
                            skillNames.add(skills.getString(i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Account> qualified = groupService.broadcastEvent(group.getGroupID(), group.getGroupLeaderID(), purpose, proximity, skillNames);
        if (qualified == null) {
            log.info("Could not create broadcast event");
            return null;
        }
        Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
        leader.log("Create event for group " + group.getGroupName() + " for purpose " + purpose);
        accountRepository.save(leader);
        return group;
    }

    @Override
    public Account addToGroup(String userId, String groupId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Group group = groupRepository.findByGroupID(groupId);

        groupService.addGroupMember(group.getGroupID(), group.getGroupLeaderID(), user.getAccountID());

        return user;
    }

    @Override
    public Account leaveGroup(String username, String groupId) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Group group = groupRepository.findByGroupID(groupId);
        if (!group.hasGroupMember(user.getAccountID())) {
            log.info("User " + username + " is not in group " + groupId);
            return null;
        }
        if (group.getGroupLeaderID().equals(user.getAccountID())) {
            if (group.getGroupMemberIDs().size() == 1) {
                user.removeGroup(groupId);
                groupRepository.delete(group);
                accountRepository.save(user);
                return user;
            } else {
                List<String> memberIDs = new ArrayList<>(group.getGroupMemberIDs().keySet());
                group.setGroupLeaderID(memberIDs.get(0));
                group.setGroupLeaderName(group.getGroupMemberIDs().get(memberIDs.get(0)));
            }

        }
        user.removeGroup(groupId);
        user.log("Leave group " + group.getGroupName());
        group.removeGroupMember(user.getAccountID());
        groupRepository.save(group);
        accountRepository.save(user);
        sendMessage(groupId, groupId, user.getFullName() + " has left the group", Types.CHAT_MESSAGE);
        return user;

    }

    @Override
    public void handleNotifications(String userId, String messageId, boolean accept) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return;
        }
        Message message = messageRepository.findByMessageID(messageId);
        System.out.println("MESSAGE: " + message.getContent() + "[" + message.getType() + "]");
        switch (message.getType()) {
            /* RATE_REQUEST HANDLED ELSEWHERE */
            case Types.GROUP_INVITE:
                if (accept) {
                    acceptGroupInvite(userId, messageId);
                } else {
                    rejectInvite(userId, messageId);
                }
                break;
            case Types.FRIEND_INVITE:
                if (accept) {
                    acceptFriendInvite(userId, messageId);
                } else {
                    rejectInvite(userId, messageId);
                }
                break;
            case Types.MOD_FLAG:
                break;
            case Types.JOIN_REQUEST:
                if (accept) {
                    acceptJoinRequest(userId, messageId);
                } else {
                    rejectJoinRequest(userId, messageId);
                }
                break;
            case Types.GROUP_ACCEPTED:
                deleteMessage(userId, messageId);
                break;
            case Types.FRIEND_ACCEPTED:
                deleteMessage(userId, messageId);
                break;
        }
    }

    @Override
    public void requestRating(String userId, String groupId) {
        groupService.initiateRatings(groupId, userId);
    }

    @Override
    public List<Account> inviteAll(String userId, String groupId) {
        return groupService.inviteEligibleUsers(groupId, userId);
    }

    @Override
    public Message requestToGroup(String userId, String leaderId, String groupId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(leaderId)) {
            log.info("User " + leaderId + " not found");
            return null;
        }
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account leader = accountRepository.findByAccountID(leaderId);
        Group group = groupRepository.findByGroupID(groupId);
        if (!group.getGroupLeaderID().contentEquals(leader.getAccountID())) {
            log.info(leaderId + " is not leader of group " + groupId);
            return null;
        }
        Message message = groupService.requestToJoinGroup(groupId, userId);
        if (message == null) {
            log.info("Could not send request to group " + groupId);
            return null;
        }
        try {
            template.convertAndSend("/notification/" + leaderId, message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Message inviteToGroup(String userId, String friendId, String groupId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(friendId)) {
            log.info("User " + friendId + " not found");
            return null;
        }
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account friend = accountRepository.findByAccountID(friendId);
        Group group = groupRepository.findByGroupID(groupId);
        user.log("Invite " + friend.getFullName() + " to group " + group.getGroupName());
        accountRepository.save(user);
        Message message = sendMessage(user.getAccountID(), friend.getAccountID(), user.getFullName() + " has invited you to join " + group.getGroupName(), Types.GROUP_INVITE);
        if (message == null) {
            log.info("Could not invite user " + friend.getFullName() + " to group " + group.getGroupName());
            return null;
        }
        message.setGroupID(groupId);
        try {
            template.convertAndSend("/notification/" + friendId, message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Message kickMember(String userId, String kickedId, String groupId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!accountRepository.existsByAccountID(kickedId)) {
            log.info("User " + kickedId + " not found");
            return null;
        }
        Account kicked = accountRepository.findByAccountID(kickedId);
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupService.removeGroupMember(groupId, userId, kickedId);
        if (group == null) {
            return null;
        }
        if (!group.getGroupLeaderID().equals(user.getAccountID())) {
            return null;
        }
        if (group.hasGroupMember(kickedId)) {
            return null;
        }
        user.log("Kick member " + kicked.getFullName() + " from group " + group.getGroupName());
        accountRepository.save(user);
        Message message = sendMessage(userId, kickedId, "You have been kicked from " + group.getGroupName(), Types.KICKED);
        try {
            template.convertAndSend("/notification/" + kickedId, message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Group deleteGroup(String username, String groupId) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        return groupService.deleteGroup(groupId, user.getAccountID());
    }

    @Override
    public double getReputationRanking(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return 0.0;
        }
        Account user = accountRepository.findByUsername(username);
        List<Account> accounts = accountRepository.findAll();
        ArrayList<Integer> reputations = new ArrayList<>();
        for (Account account : accounts) {
            reputations.add(account.getReputation());
        }
        Collections.sort(reputations);
        int rank = reputations.lastIndexOf(user.getReputation()) + 1;
        return (100.0 * rank) / reputations.size();
    }

    @Override
    public Message sendFriendInvite(String userId, String receiverId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(receiverId)) {
            log.info("User " + receiverId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account receiver = accountRepository.findByAccountID(receiverId);
        if (user.hasFriend(receiverId)) {
            log.info("User " + userId + " is already friends with " + receiverId);
            return null;
        }
        user.log("Send friend invite to " + receiver.getFullName());
        accountRepository.save(user);
        Message message = sendMessage(userId, receiverId, user.getFullName() + " wants to add you as a friend! Do you accept the friend invite?", Types.FRIEND_INVITE);
        if (message == null) {
            log.info("Could not send a friend request to " + receiverId);
            return null;
        }
        try {
            template.convertAndSend("/notification/" + receiverId, message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Message acceptFriendInvite(String userId, String inviteId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message invite = messageRepository.findByMessageID(inviteId);
        user.removeMessage(inviteId);
        user.log("Accept friend invite");
        accountRepository.save(user);
        this.addFriend(userId, invite.getSenderID());
        Message accept = sendMessage(userId, invite.getSenderID(), user.getFullName() + " added you as a friend!", Types.FRIEND_ACCEPTED);
        messageRepository.delete(invite);
        messageRepository.save(accept);

        if (accept == null) {
            log.info("Could not add friend");
            return null;
        }
        try {
            template.convertAndSend("/notification/" + invite.getSenderID(), accept);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return accept;
    }

    @Override
    public Message acceptJoinRequest(String userId, String inviteId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message invite = messageRepository.findByMessageID(inviteId);
        if (!groupRepository.existsByGroupID(invite.getGroupID())) {
            log.info("Could not find group within message");
            return null;
        }
        Group group = groupRepository.findByGroupID(invite.getGroupID());
        Message accepted = groupService.acceptJoinRequest(userId, inviteId);
        sendMessage(group.getGroupID(), group.getGroupID(), user.getFullName() + " has joined the group!", Types.CHAT_MESSAGE);

        try {
            template.convertAndSend("/notification/" + invite.getSenderID(), accepted);
        } catch (Exception e) {
            log.info("Could not send message");
        }

        return accepted;
    }

    @Override
    public Message rejectJoinRequest(String userId, String inviteId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message invite = messageRepository.findByMessageID(inviteId);
        if (!groupRepository.existsByGroupID(invite.getGroupID())) {
            log.info("Could not find group within message");
            return null;
        }
        return groupService.denyJoinRequest(userId, inviteId);
    }

    @Override
    public Message acceptSearchInvite(String userId, String inviteId) {
        Message message = groupService.acceptSearchInvite(userId, inviteId);
        if (message == null) {
            log.info("Could not accept invite");
            return null;
        }
        Group group = groupRepository.findByGroupID(message.getGroupID());
        try {
            template.convertAndSend("/notification/" + group.getGroupLeaderID(), message);
        } catch (Exception e) {
            log.info("Could not send message");
        }
        return message;
    }

    @Override
    public Message acceptGroupInvite(String userId, String inviteId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message invite = messageRepository.findByMessageID(inviteId);
        if (!groupRepository.existsByGroupID(invite.getGroupID())) {
            log.info("Group " + invite.getGroupID() + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(invite.getGroupID());

        user.removeMessage(inviteId);
        user.log("Accept group invite for group " + group.getGroupName());
        accountRepository.save(user);
        sendMessage(group.getGroupID(), group.getGroupID(), user.getFullName() + " has joined the group!", Types.CHAT_MESSAGE);
        addToGroup(userId, group.getGroupID());
        messageRepository.delete(inviteId);
        return invite;
    }

    @Override
    public Message rejectInvite(String userId, String inviteId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message message = messageRepository.findByMessageID(inviteId);
        user.removeMessage(inviteId);
        user.log("Reject invite");
        messageRepository.delete(inviteId);
        accountRepository.save(user);
        return message;
    }

    @Override
    public Account addFriend(String userId, String friendId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(friendId)) {
            log.info("User " + friendId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account friend = accountRepository.findByAccountID(friendId);
        user.addFriend(friend);
        friend.addFriend(user);
        user.log("Add friend " + friend.getFullName());
        friend.log("Add friend " + user.getFullName());
        accountRepository.save(user);
        accountRepository.save(friend);
        return friend;
    }

    @Override
    public Account removeFriend(String username, String friendId) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(friendId)) {
            log.info("User " + friendId + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        Account friend = accountRepository.findByAccountID(friendId);
        user.removeFriend(friend.getAccountID());
        friend.removeFriend(user.getAccountID());
        user.log("Remove friend " + friend.getFullName());
        friend.log("Remove friend " + user.getFullName());
        accountRepository.save(user);
        accountRepository.save(friend);
        return friend;
    }

    @Override
    public String checkNewUserFlag(String username) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        user.incrementTimer();
        accountRepository.save(user);
        if (user.isNewUser()) {
            return "New User";
        }
        return "Experienced User";
    }

    @Override
    public Account setAccountSettings(String username, String json) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        try {
            ObjectMapper om = new ObjectMapper();
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            Map<String, String> map = om.readValue(json, typeRef);
            for (String key : map.keySet()) {
                switch (key) {
                    case "isPublic":
                        Boolean isPublic = Boolean.parseBoolean(map.get("isPublic"));
                        user.setPublic(isPublic);
                        break;
                    case "isOptedOut":
                        Boolean isOptedOut = Boolean.parseBoolean(map.get("isOptedOut"));
                        user.setOptedOut(isOptedOut);
                        break;
                    case "reputationReq":
                        Double repReq = Double.parseDouble(map.get("reputationReq"));
                        user.setReputationReq(repReq / user.getReputation());
                        break;
                    case "proximityReq":
                        Integer proxReq = Integer.parseInt(map.get("proximityReq"));
                        user.setProximityReq(proxReq);
                        break;
                }
            }
            accountRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Group setGroupSettings(String username, String groupId, String json) {
        if (!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        Account user = accountRepository.findByUsername(username);
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if (!group.getGroupLeaderID().equals(user.getAccountID())) {
            log.info("User does not have right to change settings");
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            for (Object k : obj.keySet()) {
                String key = k.toString();
                if (key.contentEquals("skillsReq")) {
                    JSONArray skills = obj.getJSONArray("skillsReq");
                    for (int i = 0; i < skills.length(); i++) {
                        if (skillRepository.existsBySkillName(skills.getString(i))) {
                            Skill skill = skillRepository.findBySkillNameAndSkillLevel(skills.getString(i), 0);
                            group.addSkillReq(skill);
                        }
                    }
                    groupRepository.save(group);
                }
                groupService.updateGroupSettings(groupId, user.getAccountID(), key, obj.get(key).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    @Override
    public Account rateUser(String userId, String rateeId, String messageId, String json, boolean endorse) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(rateeId)) {
            log.info("User " + rateeId + " not found");
            return null;
        }
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Could not find rate request");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account ratee = accountRepository.findByAccountID(rateeId);
        Message message = messageRepository.findByMessageID(messageId);

        user.removeMessage(messageId);
        messageRepository.delete(message.getMessageID());
        user.log("Rate user " + ratee.getFullName());
        accountRepository.save(user);

        Map<String, Integer> map = null;
        try {
            map = new TreeMap<>();
            JSONObject obj = new JSONObject(json);
            for (Object k : obj.keySet()) {
                String key = k.toString();
                Integer level = Integer.parseInt(obj.getString(key));
                map.put(key, level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map == null) {
            log.info("Map is null");
            return null;
        }
        groupService.rateGroupMember(message.getGroupID(), userId, rateeId, endorse, map);
        ratee.setRank(getReputationRanking(ratee.getUsername()));
        checkModStatus(ratee.getAccountID());
        return ratee;
    }

    @Override
    public void addToModerators (String userId){
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        Message congrats = new Message(userId, user.getFullName(),"Congratulations! You're a moderator now!", Types.MOD_ACCEPTED);
        user.setAuthorities(getModRoles());
        user.setModerator(true);
        user.addMessage(congrats);
        user.log("Add moderator status");
        accountRepository.save(user);
        try {
            template.convertAndSend("/notification/" + userId, congrats);
        } catch (Exception e) {
            log.info("Could not send message");
        }
    }

    // TODO: update for decided rules
    @Override
    public void checkModStatus (String userId) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        if (!user.isModerator()) { //if user is not already a moderator
            if (!user.isDeniedMod()) { //if user already denied being a moderator
                if (!user.isNewUser()) { //if user is not a new user
                    if (user.getReputation() >= 50) { //if user has reputation higher than 50
                        if (user.getGroupIDs().keySet().size() > 5) { //user has to be in at least 5 groups
                            Message offer = new Message(userId, user.getFullName(),"You are now able to apply to be a moderator!", Types.MOD_INVITE);
                            user.addMessage(offer);
                            messageRepository.save(offer);
                            accountRepository.save(user);
                            try {
                                template.convertAndSend("/notification/" + userId, offer);
                            } catch (Exception e) {
                                log.info("Could not send message");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void flagUser(String modId, String messageId) {
        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return;
        }
        Message report = messageRepository.findByMessageID(messageId);

        if (!accountRepository.existsByAccountID(modId)) {
            log.info("User " + modId + " not found");
            return;
        }
        Account mod = accountRepository.findByAccountID(modId);

        if (!accountRepository.existsByAccountID(report.getTopicID())) {
            log.info("User " + report.getTopicID() + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(report.getTopicID());

        if (!mod.isModerator()) {
            log.info(mod.getFullName() + " is not a moderator");
            mod.log("Attempted to use moderator tool");
            accountRepository.save(mod);
            return;
        }
        if (mod.haveFlagged(user.getAccountID())) {
            user.removeFlag();
        } else {
            user.addFlag();
        }
        mod.toggleFlag(user.getAccountID());
        accountRepository.save(user);
        accountRepository.save(mod);
        report.setRead(true);
        messageRepository.save(report);
        return;
    }

    @Override
    public void suspendUser(String modId, String messageId, long minutes) {
        if (!accountRepository.existsByAccountID(modId)) {
            log.info("User " + modId + " not found");
            return;
        }
        Account mod = accountRepository.findByAccountID(modId);

        if (!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return;
        }
        Message report = messageRepository.findByMessageID(messageId);

        if (!accountRepository.existsByAccountID(report.getTopicID())) {
            log.info("User " + report.getTopicID() + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(report.getTopicID());

        if (!mod.isModerator()) {
            log.info(mod.getFullName() + " is not a moderator");
            mod.log("Attempted to use moderator tool");
            accountRepository.save(mod);
            return;
        }
        if (user.isAccountEnabled()) {
            user.suspend(minutes);
            accountRepository.save(user);
        }
        deleteMessageByParent(report.getParentID());
    }

    @Override
    public void reportUser(String userId, String reporteeId, String reason) {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return;
        }
        if (!accountRepository.existsByAccountID(reporteeId)) {
            log.info("User " + reporteeId + " not found");
            return;
        }
        Account user = accountRepository.findByAccountID(userId);
        Account reportee = accountRepository.findByAccountID(reporteeId);
        user.log("Report user " + reportee.getFullName());
        reportee.log("Reported by user " + user.getFullName());
        accountRepository.save(user);
        accountRepository.save(reportee);
        Message message = new Message(user.getAccountID(), user.getFullName(), reason, Types.MOD_REPORT);
        message.setParentID("Report:" + reportee.getAccountID());
        message.setTopicID(reportee.getAccountID());
        sendMessageToMods(user.getAccountID(), message);
    }

    @Override
    public List<String> getActivityLog(String modId, String userId, String startDate, String endDate)
    {
        if (!accountRepository.existsByAccountID(modId)) {
            log.info("Moderator " + modId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account moderator = accountRepository.findByAccountID(modId);
        if(!moderator.isModerator()) {
            log.info("Account " + modId + " is not a moderator");
            moderator.log("Attempted to use moderator tool");
            accountRepository.save(moderator);
            return null;
        }
        Account account = accountRepository.findByAccountID(userId);
        if(startDate == null && endDate == null) {
            return account.getLogs();
        }
        if(startDate == null){
            startDate = "2000-01-01";
        }
        if(endDate == null){
            endDate = LocalDate.now().toString();
        }
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<String> log = new ArrayList<>();
        for(String info : account.getLogs()) {
            LocalDate date = LocalDate.parse(info.substring(info.length()-10));
            if(!date.isBefore(start) && !date.isAfter(end)) {
                log.add(info);
            }
        }
        return log;
    }

    @Override
    public Message reportGroupMember(String groupId, String reporterId, String messageId, String reason) {
        if (!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if (!group.hasGroupMember(reporterId)) {
            log.info("User " + reporterId + " is not in group " + groupId);
            return null;
        }
        if (!group.hasMessage(messageId)) {
            log.info("Group " + groupId + " does not contain message " + messageId);
            return null;
        }
        Message message = messageRepository.findByMessageID(messageId);
        Account reporter = accountRepository.findByAccountID(reporterId);
        Account reportee = accountRepository.findByAccountID(message.getSenderID());
        reporter.log("Report user " + reportee.getFullName());
        reportee.log("Reported by user " + reporter.getFullName());
        accountRepository.save(reporter);
        accountRepository.save(reportee);
        Message report = new Message(reporterId, reporter.getFullName(), reason, Message.Types.MOD_REPORT);
        report.setParentID("Report:" + reportee.getAccountID());
        report.setGroupID(groupId);
        report.setTopicID(reportee.getAccountID());
        report.setChatMessageID(messageId);
        sendMessageToMods(reporterId, report);
        return report;
    }

    @Override
    public List<Message> getReportContext(String modId, String messageId, String startId, String endId) {
        if (!accountRepository.existsByAccountID(modId)) {
            log.info("Moderator " + modId + " not found");
            return null;
        }
        Account moderator = accountRepository.findByAccountID(modId);
        if(!moderator.isModerator()) {
            log.info("Account " + modId + " is not a moderator");
            moderator.log("Attempted to use moderator tool");
            accountRepository.save(moderator);
            return null;
        }
        if(!messageRepository.existsByMessageID(messageId)) {
            log.info("Message " + messageId + " not found");
            return null;
        }
        if(!moderator.hasMessage(messageId)) {
            log.info("Moderator " + modId + " does not have message " + messageId);
            return null;
        }
        Message report = messageRepository.findByMessageID(messageId);

        if(report.getChatMessageID() == null) {
            log.info("Report " + messageId + " does not pertain to a group chat");
            return null;
        }
        Group group = groupRepository.findByGroupID(report.getGroupID());

        if (!messageRepository.existsByMessageID(startId)) {
            log.info("Could not find lower boundary message");
            startId = report.getChatMessageID();
        }
        if (!messageRepository.existsByMessageID(endId)) {
            log.info("Could not find upper boundary message");
            endId = report.getChatMessageID();
        }

        int start = Math.max(group.getChatMessageIDs().indexOf(startId)-5, 0);
        int end = Math.min(group.getChatMessageIDs().indexOf(endId)+5, group.getChatMessageIDs().size()-1);
        List<Message> context = new ArrayList<>();
        for(int i = start; i <= end; i++) {
            context.add(messageRepository.findByMessageID(group.getChatMessageIDs().get(i)));
        }
        return context;
    }

    @Override
    public List<Message> getMessageHistory(String modId, String userId) {
        if (!accountRepository.existsByAccountID(modId)) {
            log.info("Moderator " + modId + " not found");
            return null;
        }
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account moderator = accountRepository.findByAccountID(modId);
        if(!moderator.isModerator()) {
            log.info("Account " + modId + " is not a moderator");
            moderator.log("Attempted to use moderator tool");
            accountRepository.save(moderator);
            return null;
        }
        return messageRepository.findBySenderID(userId);
    }

    /* TODO: Finish upload picture once Jordan knows how it will be sent */
    @Override
    public void uploadPicture(String userId, MultipartFile file) throws Exception {
        if (!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            throw new UsernameNotFoundException("Could not find user");
        }
        Account user = accountRepository.findByAccountID(userId);

        if (!file.isEmpty()) {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(picturePath, file.getOriginalFilename())));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
        }
        user.setPicturePath(picturePath + "/" + file.getOriginalFilename());
        accountRepository.save(user);
    }
}