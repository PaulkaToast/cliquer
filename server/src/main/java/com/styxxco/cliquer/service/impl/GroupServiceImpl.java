package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Log4j
@Service(value = GroupServiceImpl.NAME)
public class GroupServiceImpl implements GroupService {
    public final static String NAME = "GroupService";

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
    private SimpMessagingTemplate template;

    public GroupServiceImpl() {

    }

    public GroupServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr) {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
    }

    @Override
    public Group createGroup(String groupName, String groupPurpose, String groupLeaderId) {
        if(!accountRepository.existsByAccountID(groupLeaderId)) {
            log.info("User " + groupLeaderId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(groupLeaderId);
        Group group = new Group(groupName, groupPurpose, user.getAccountID(), user.getFullName());
        this.groupRepository.save(group);
        user.addGroup(group);
        user.log("Create group " + group.getGroupName());
        this.accountRepository.save(user);
        return group;
    }

    @Override
    public Group deleteGroup(String groupId, String groupLeaderId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        for(String accountID : group.getGroupMemberIDs().keySet()) {
            Account account = accountRepository.findByAccountID(accountID);
            account.removeGroup(groupId);
            accountRepository.save(account);
        }
        for(String messageID : group.getChatMessageIDs()){
            messageRepository.delete(messageID);
        }
        groupRepository.delete(group);
        leader.log("Delete group " + group.getGroupName());
        leader.removeGroup(groupId);
        accountRepository.save(leader);
        return group;

    }

    @Override
    public Group getUserGroup(String groupId, String accountId) {
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(group.getGroupMemberIDs().keySet().contains(accountId)) {
            return group;
        }
        log.info("User " + accountId + " is not a member of group " + groupId);
        return null;
    }

    @Override
    public Group getPublicGroup(String groupId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.isPublic()) {
            log.info("Group " + groupId + " is a private group");
            return null;
        }
        return group;
    }

    @Override
    public Group updateGroupSettings(String groupId, String groupLeaderId, String field, String value) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        switch(field) {
            case "groupName" : group.setGroupName(value); break;
            case "groupPurpose" : group.setGroupPurpose(value); break;
            case "isPublic" : group.setPublic(Boolean.parseBoolean(value)); break;
            case "reputationReq" :
                double repReq;
                try {
                    repReq = Double.parseDouble(value);
                } catch(NumberFormatException e) {
                    log.info("Invalid reputation requirement");
                    return null;
                }
                group.setReputationReq(repReq / accountRepository.findByAccountID(groupLeaderId).getReputation());
                break;
            case "proximityReq" :
                int proxReq;
                try {
                    proxReq = Integer.parseInt(value);
                } catch(NumberFormatException e) {
                    log.info("Invalid proximity requirement");
                    return null;
                }
                if(proxReq <= 0) {
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
    public Group addGroupMember(String groupId, String groupLeaderId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(group.hasGroupMember(accountId)) {
            log.info("User " + accountId + " is already in group " + groupId);
            return null;
        }
        Account member = accountRepository.findByAccountID(accountId);
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        group.addGroupMember(member);
        groupRepository.save(group);
        member.addGroup(group);
        leader.log("Add " + member.getFullName() + " to group " + group.getGroupName());
        accountRepository.save(leader);
        accountRepository.save(member);
        return group;
    }

    @Override
    public Group removeGroupMember(String groupId, String groupLeaderId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountId)) {
            log.info("User " + accountId + " is not in group " + groupId);
            return null;
        }
        group.removeGroupMember(accountId);
        groupRepository.save(group);
        Account member = accountRepository.findByAccountID(accountId);
        member.removeGroup(groupId);
        accountRepository.save(member);
        return group;
    }

    @Override
    public Group startVoteKick(String groupId, String groupLeaderId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        if(group.getKickCandidate() != null) {
            log.info("User " + groupLeaderId + " has an ongoing vote kick");
            return null;
        }
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountId)) {
            log.info("User " + accountId + " is not in group " + groupId);
            return null;
        }
        group.setKickCandidate(accountId);
        group.setKickVotes(new ArrayList<>());
        group.addKickVote(groupLeaderId);
        groupRepository.save(group);
        if(group.getKickVotes().size()*2 >= group.getGroupMemberIDs().size() - 1) {
            group = this.removeGroupMember(groupId, groupLeaderId, accountId);
            group.setKickCandidate(null);
            group.setKickVotes(new ArrayList<>());
            groupRepository.save(group);
        }
        return group;
    }

    @Override
    public Group closeVoteKick(String groupId, String groupLeaderId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        if(group.getKickCandidate() == null) {
            log.info("User " + groupLeaderId + " has not started a vote kick");
            return null;
        }
        group.setKickCandidate(null);
        group.setKickVotes(new ArrayList<>());
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group acceptVoteKick(String groupId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountId)) {
            log.info("User " + accountId + " is not in group " + groupId);
            return null;
        }
        if(group.getKickCandidate() == null) {
            log.info("Group " + groupId + " has no ongoing vote kick");
            return null;
        }
        if(accountId.equals(group.getKickCandidate())) {
            log.info("User " + accountId + " is the one being vote kicked");
            return null;
        }
        if(accountId.equals(group.getGroupLeaderID())) {
            log.info("User " + accountId + " started the vote kick and cannot change vote");
            return null;
        }
        if(group.hasKickVote(accountId)) {
            log.info("User " + accountId + " has already voted to kick " + group.getKickCandidate());
            return null;
        }
        group.addKickVote(accountId);
        groupRepository.save(group);
        if(group.getKickVotes().size()*2 >= group.getGroupMemberIDs().size() - 1) {
            group = this.removeGroupMember(groupId, group.getGroupLeaderID(), group.getKickCandidate());
            group.setKickCandidate(null);
            group.setKickVotes(new ArrayList<>());
            groupRepository.save(group);
        }
        return group;
    }

    @Override
    public Group denyVoteKick(String groupId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountId)) {
            log.info("User " + accountId + " is not in group " + groupId);
            return null;
        }
        if(group.getKickCandidate() == null) {
            log.info("Group " + groupId + " has no ongoing vote kick");
            return null;
        }
        if(accountId.equals(group.getKickCandidate())) {
            log.info("User " + accountId + " is the one being vote kicked");
            return null;
        }
        if(accountId.equals(group.getGroupLeaderID())) {
            log.info("User " + accountId + " started the vote kick and cannot change vote");
            return null;
        }
        if(!group.hasKickVote(accountId)) {
            log.info("User " + accountId + " has already chosen to not kick " + group.getKickCandidate());
            return null;
        }
        group.removeKickVote(accountId);
        groupRepository.save(group);
        return group;
    }

    @Override
    public List<Group> searchBySettings(String username, List<Group> groups) {
        if(!accountRepository.existsByUsername(username)) {
            log.info("User " + username + " not found");
            return null;
        }
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        Account user = accountRepository.findByUsername(username);
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            boolean exit = false;
            for(String member : group.getGroupMemberIDs().keySet()) {
                if(member.equals(user.getAccountID())) {
                    exit = true;
                    break;
                }
            }
            if(exit) {
                continue;
            } else if(!group.isPublic()) {
                continue;
            }
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());

            if(leader == null) {
                continue;
            } else if(group.getReputationReq()*leader.getReputation() > user.getReputation() || user.getReputationReq() * user.getReputation() > leader.getReputation()) {
                continue;
            } else if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > user.getProximityReq()) {
                continue;
            }
            qualified.add(group);
        }
        Comparator<Group> byGroupName = Comparator.comparing(Group::getGroupName);
        qualified.sort(byGroupName);
        return qualified;
    }

    @Override
    public List<Group> searchBySkillReqs(List<String> skillRequirements, List<Group> groups) {
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            List<Skill> skills = this.getAllSkillReqs(group.getGroupID());
            boolean exit = false;
            for(String skillName : skillRequirements) {
                int i;
                for(i = 0; i < skills.size(); i++) {
                    if(skills.get(i).getSkillName().equals(skillName)) {
                        break;
                    }
                }
                if(i == skills.size()) {
                    exit = true;
                    break;
                }
            }
            if(!exit) {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByGroupName(String groupName, List<Group> groups) {
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            if(group.getGroupName().toLowerCase().equals(groupName.toLowerCase())) {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderFirstName(String firstName, List<Group> groups) {
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getFirstName().toLowerCase().equals(firstName.toLowerCase())) {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderLastName(String lastName, List<Group> groups) {
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getLastName().toLowerCase().equals(lastName.toLowerCase())) {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderFullName(String firstName, String lastName, List<Group> groups) {
        if(groups == null) {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups) {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getFirstName().toLowerCase().equals(firstName.toLowerCase()) &&
                    leader.getLastName().toLowerCase().equals(lastName.toLowerCase())) {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Skill> getAllSkillReqs(String groupId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        List<Skill> skills = new ArrayList<>();
        for(String skillID : group.getSkillReqs().keySet()) {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        Collections.sort(skills);
        return skills;
    }

    @Override
    public Skill getSkillReq(String groupId, String skillName) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        List<Skill> skills = this.getAllSkillReqs(groupId);
        for(Skill skill : skills) {
            if(skill.getSkillName().equals(skillName)) {
                return skill;
            }
        }
        return null;
    }

    @Override
    public Group addSkillReq(String groupId, String groupLeaderId, String skillName, int skillLevel) {
        if(!skillRepository.existsBySkillName(skillName)) {
            log.info("Skill " + skillName + " is invalid");
            return null;
        }
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        if(this.getSkillReq(groupId, skillName) != null) {
            log.info("Group " + groupId + " already has skill requirement " + skillName);
            return null;
        }
        if(skillLevel < 0 || skillLevel > 10) {
            log.info("Skill level " + skillLevel + " is invalid");
            return null;
        }
        Skill skill = new Skill(skillName, skillLevel);
        skillRepository.save(skill);
        group.addSkillReq(skill);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group removeSkillReq(String groupId, String groupLeaderId, String skillName) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        Skill skill = this.getSkillReq(groupId, skillName);
        if(skill == null) {
            log.info("Group " + groupId + " does not have skill requirement " + skillName);
            return null;
        }
        skillRepository.delete(skill);
        group.removeSkillReq(skill.getSkillID());
        groupRepository.save(group);
        return group;
    }

    @Override
    public boolean meetsGroupRequirements(String groupId, String accountId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return false;
        }
        if(!accountRepository.existsByAccountID(accountId)) {
            log.info("User " + accountId + " not found");
            return false;
        }
        Group group = groupRepository.findByGroupID(groupId);
        Account user = accountRepository.findByAccountID(accountId);
        Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
        if(group.hasGroupMember(user.getAccountID())) {
            log.info("User " + accountId + " is already a member of group " + groupId);
            return false;
        }
        if(group.getReputationReq()*leader.getReputation() > user.getReputation()) {
            return false;
        }
        if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > group.getProximityReq()) {
            return false;
        }
        boolean metReq = true;
        for(String skillReqID : group.getSkillReqs().keySet()) {
            metReq = false;
            Skill skillReq = skillRepository.findBySkillID(skillReqID);
            for(String skillID : user.getSkillIDs().keySet()) {
                Skill skill = skillRepository.findBySkillID(skillID);
                if(skill.getSkillName().equals(skillReq.getSkillName())) {
                    if(skill.getSkillLevel() >= skillReq.getSkillLevel()) {
                        metReq = true;
                    }
                    break;
                }
            }
            if(!metReq) {
                break;
            }
        }
        return metReq;
    }

    @Override
    public Message acceptSearchInvite(String userId, String inviteId) {
        if(!accountRepository.existsByAccountID(userId)) {
            log.info("User " + userId + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(userId);
        if(!user.hasMessage(inviteId)) {
            log.info("User " + userId + " did not receive message " + inviteId);
            return null;
        }
        Message invite = messageRepository.findByMessageID(inviteId);
        messageRepository.delete(invite);
        user.removeMessage(inviteId);
        accountRepository.save(user);
        return this.requestToJoinGroup(invite.getGroupID(), userId);
    }

    @Override
    public Message requestToJoinGroup(String groupId, String accountId) {
        if(!this.meetsGroupRequirements(groupId, accountId)) {
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        Account user = accountRepository.findByAccountID(accountId);
        Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
        Message joinRequest = new Message(accountId, user.getFullName(),
                "User " + user.getFullName() + " wishes to join your group " + group.getGroupName(),
                Message.Types.JOIN_REQUEST);
        joinRequest.setGroupID(groupId);
        messageRepository.save(joinRequest);
        user.log("Request to join group " + group.getGroupName());
        leader.addMessage(joinRequest);
        accountRepository.save(leader);
        accountRepository.save(user);
        return joinRequest;
    }

    @Override
    public Message acceptJoinRequest(String groupLeaderId, String messageId) {
        if(!accountRepository.existsByAccountID(groupLeaderId)) {
            log.info("User " + groupLeaderId + " not found");
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        if(!leader.hasMessage(messageId)) {
            log.info("User " + groupLeaderId + " did not receive message " + messageId);
            return null;
        }
        Message request = messageRepository.findByMessageID(messageId);
        messageRepository.delete(request);
        leader.removeMessage(messageId);
        accountRepository.save(leader);
        if(!accountRepository.existsByAccountID(request.getSenderID())) {
            log.info("User " + request.getSenderID() + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(request.getGroupID())) {
            log.info("Group " + request.getGroupID() + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(request.getGroupID());
        Account sender = accountRepository.findByAccountID(request.getSenderID());
        group.addGroupMember(sender);
        groupRepository.save(group);
        leader.log("Accept join request from " + sender.getFullName() + " for group " + group.getGroupName());
        accountRepository.save(leader);
        Message acceptance = new Message(groupLeaderId, leader.getFullName(),
                "You have been accepted into group " + group.getGroupName(),
                Message.Types.GROUP_ACCEPTED);
        messageRepository.save(acceptance);
        sender.addMessage(acceptance);
        sender.addGroup(group);
        accountRepository.save(sender);
        return acceptance;
    }

    @Override
    public Message denyJoinRequest(String groupLeaderId, String messageId) {
        if(!accountRepository.existsByAccountID(groupLeaderId)) {
            log.info("User " + groupLeaderId + " not found");
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        if(!leader.hasMessage(messageId)) {
            log.info("User " + groupLeaderId + " did not receive message " + messageId);
            return null;
        }
        Message request = messageRepository.findByMessageID(messageId);
        if (!accountRepository.existsByAccountID(request.getSenderID())) {
            log.info("User " + request.getSenderID() + " not found");
            return null;
        }
        Account sender = accountRepository.findByAccountID(request.getSenderID());
        sender.removeMessage(messageId);
        messageRepository.delete(request);
        leader.removeMessage(messageId);
        leader.log("Deny join request from " + sender.getFullName());
        accountRepository.save(leader);
        accountRepository.save(sender);
        return request;
    }

    @Override
    public void initiateRatings(String groupId, String groupLeaderId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        if(!group.startMemberRatings()) {
            log.info("Group " + groupId + " has maxed out the limit for group ratings");
            return;
        }
        groupRepository.save(group);

        for(String accountID : group.getGroupMemberIDs().keySet()) {
            if(accountID.equals(groupLeaderId)) {
                continue;
            }
            Message message = new Message(groupLeaderId, leader.getFullName(),
                    "You can now rate your fellow members in group " + group.getGroupName() + "!", Message.Types.RATE_REQUEST);
            message.setGroupID(groupId);
            messageRepository.save(message);
            Account member = accountRepository.findByAccountID(accountID);
            member.addMessage(message);
            accountRepository.save(member);

            try {
                template.convertAndSend("/notification/" + member.getAccountID(), message);
            } catch (Exception e) {
                log.info("Could not send message");
            }
        }
    }

    @Override
    public Map<String, Integer> getGroupMemberRatingForm(String groupId, String rateeId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.hasGroupMember(rateeId)) {
            log.info("User " + rateeId + " is not in group " + groupId);
            return null;
        }
        Account member = accountRepository.findByAccountID(rateeId);
        Map<String, Integer> form = new TreeMap<>();
        for(String skillID : group.getSkillReqs().keySet()) {
            for(String id : member.getSkillIDs().keySet()) {
                String skillReq = skillRepository.findBySkillID(skillID).getSkillName();
                String skillName = skillRepository.findBySkillID(id).getSkillName();
                if(skillReq.equals(skillName)) {
                    form.put(skillName, 0);
                    break;
                }
            }
        }
        return form;
    }

    @Override
    public String rateGroupMember(String groupId, String raterId, String rateeId, boolean endorse, Map<String, Integer> skillRatings) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.hasGroupMember(raterId)) {
            log.info("User " + raterId + " is not in group " + groupId);
            return null;
        }
        if(!group.hasGroupMember(rateeId)) {
            log.info("User " + raterId + " is not in group " + groupId);
            return null;
        }
        if(!group.canGiveRating(raterId, rateeId)) {
            log.info("User " + raterId + " cannot rate user " + rateeId);
            return null;
        }
        Account member = accountRepository.findByAccountID(rateeId);
        List<Skill> skills = new ArrayList<>();
        List<Integer> ratings = new ArrayList<>();
        for(String skillName : skillRatings.keySet()) {
            if(skillRatings.get(skillName) > 0 && skillRatings.get(skillName) <= 10) {
                for(Map.Entry<String, String> entry : member.getSkillIDs().entrySet()) {
                    if(entry.getValue().equals(skillName)) {
                        skills.add(skillRepository.findBySkillID(entry.getKey()));
                        ratings.add(skillRatings.get(skillName));
                        break;
                    }
                }
            }
        }
        Map<String, Integer> updatedSkills = member.addSkillRatings(skills, ratings);
        for(Map.Entry<String, Integer> entry : updatedSkills.entrySet()) {
            Skill skill = skillRepository.findBySkillNameAndSkillLevel(entry.getKey(), entry.getValue());
            member.addSkill(skill);
        }
        Account rater = accountRepository.findByAccountID(raterId);
        if(endorse) {
            int reputation = member.getReputation();
            reputation += (2 + rater.getReputation()/15);
            reputation = Math.min(reputation, 100);
            member.setReputation(reputation);
        }
        rater.log("Rate user " + member.getFullName());
        accountRepository.save(rater);
        accountRepository.save(member);
        groupRepository.save(group);
        return "Success";
    }

    @Override
    public List<Account> broadcastEvent(String groupId, String groupLeaderId, String description, int proximity, List<String> skillNames) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        List<Account> accounts = accountRepository.findAll();
        List<Account> qualified = new ArrayList<>();
        for(Account account : accounts) {
            if(group.getGroupMemberIDs().containsKey(account.getAccountID())) {
                continue;
            }
            if(!account.isPublic() || account.isOptedOut()) {
                continue;
            }
            if(account.distanceTo(leader.getLatitude(), leader.getLongitude()) > proximity) {
                continue;
            }
            boolean exit = false;
            for(String skillName : skillNames) {
                if(!account.getSkillIDs().containsValue(skillName)) {
                    exit = true;
                    break;
                }
            }
            if(exit) {
                continue;
            }
            Message invite = new Message(groupLeaderId, leader.getFullName(),
                    "You have been invited to an event hosted by group " + group.getGroupName() + "! Here are the details: " + description,
                    Message.Types.EVENT_INVITE);
            invite.setGroupID(groupId);
            account.addMessage(invite);
            accountRepository.save(account);
            qualified.add(account);

            try {
                template.convertAndSend("/notification/" + account.getAccountID(), invite);
            } catch (Exception e) {
                log.info("Could not send message");
            }
        }
        leader.log("Create event for group " + group.getGroupName() + " for purpose " + description);
        accountRepository.save(leader);
        return qualified;
    }

    @Override
    public List<Account> inviteEligibleUsers(String groupId, String groupLeaderId) {
        if(!groupRepository.existsByGroupID(groupId)) {
            log.info("Group " + groupId + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupId);
        if(!group.getGroupLeaderID().equals(groupLeaderId)) {
            log.info("User " + groupLeaderId + " is not the leader of group " + groupId);
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderId);
        List<Account> accounts = accountRepository.findAll();
        List<Account> qualified = new ArrayList<>();
        for(Account account : accounts) {
            if(group.getGroupMemberIDs().containsKey(account.getAccountID())) {
                continue;
            }
            if(account.isOptedOut()) {
                continue;
            }
            if(leader.getReputation() < account.getReputation() * account.getReputationReq()) {
                continue;
            }
            if(account.distanceTo(leader.getLatitude(), leader.getLongitude()) > account.getProximityReq()) {
                continue;
            }
            if(!meetsGroupRequirements(groupId, account.getAccountID())) {
                continue;
            }
            Message invite = new Message(groupLeaderId, leader.getFullName(),
                    "You have been matched with group " + group.getGroupName() +"!",
                    Message.Types.SEARCH_INVITE);
            invite.setGroupID(groupId);
            messageRepository.save(invite);
            account.addMessage(invite);
            accountRepository.save(account);
            qualified.add(account);

            try {
                template.convertAndSend("/notification/" + account.getAccountID(), invite);
            } catch (Exception e) {
                log.info("Could not send message");
            }
        }
        return qualified;
    }
}
