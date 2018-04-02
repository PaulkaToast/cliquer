package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AccountService accountService;

    public GroupServiceImpl() {

    }

    public GroupServiceImpl(AccountRepository ar, SkillRepository sr, MessageRepository mr, GroupRepository gr)
    {
        this.accountRepository = ar;
        this.skillRepository = sr;
        this.messageRepository = mr;
        this.groupRepository = gr;
    }

    @Override
    public Group createGroup(String groupName, String groupPurpose, String groupLeaderID)
    {
        if(!accountRepository.existsByAccountID(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " not found");
            return null;
        }
        Account user = accountRepository.findByAccountID(groupLeaderID);
        Group group = new Group(groupName, groupPurpose, user.getAccountID(), user.getFullName());
        this.groupRepository.save(group);
        user.addGroup(group);
        this.accountRepository.save(user);
        return group;
    }

    @Override
    public Group deleteGroup(String groupID, String groupLeaderID)
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
        for(String accountID : group.getGroupMemberIDs().keySet())
        {
            Account account = accountRepository.findByAccountID(accountID);
            account.removeGroup(groupID);
            accountRepository.save(account);
        }
        groupRepository.delete(group);
        return group;

    }

    @Override
    public Group getUserGroup(String groupID, String accountID)
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
        for(String id : group.getGroupMemberIDs().keySet())
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
    public Group getPublicGroup(String groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.isPublic())
        {
            log.info("Group " + groupID + " is a private group");
            return null;
        }
        return group;
    }

    @Override
    public Group updateGroupSettings(String groupID, String groupLeaderID, String field, String value)
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
            case "isPublic" : group.setPublic(Boolean.parseBoolean(value)); break;
            case "reputationReq" :
                double repReq;
                try {
                    repReq = Double.parseDouble(value);
                }
                catch(NumberFormatException e) {
                    log.info("Invalid reputation requirement");
                    return null;
                }
                group.setReputationReq(repReq / accountRepository.findByAccountID(groupLeaderID).getReputation());
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
    public Group addGroupMember(String groupID, String groupLeaderID, String accountID)
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
        if(group.hasGroupMember(accountID))
        {
            log.info("User " + accountID + " is already in group " + groupID);
            return null;
        }
        Account member = accountRepository.findByAccountID(accountID);
        group.addGroupMember(member);
        groupRepository.save(group);
        member.addGroup(group);
        accountRepository.save(member);
        return group;
    }

    @Override
    public Group removeGroupMember(String groupID, String groupLeaderID, String accountID)
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
        if(!group.hasGroupMember(accountID))
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
    public Group startVoteKick(String groupID, String groupLeaderID, String accountID)
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
        if(group.getKickCandidate() != null)
        {
            log.info("User " + groupLeaderID + " has an ongoing vote kick");
            return null;
        }
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountID))
        {
            log.info("User " + accountID + " is not in group " + groupID);
            return null;
        }
        group.setKickCandidate(accountID);
        group.setKickVotes(new ArrayList<>());
        group.addKickVote(groupLeaderID);
        groupRepository.save(group);
        if(group.getKickVotes().size()*2 >= group.getGroupMemberIDs().size() - 1)
        {
            group = this.removeGroupMember(groupID, groupLeaderID, accountID);
            group.setKickCandidate(null);
            group.setKickVotes(new ArrayList<>());
            groupRepository.save(group);
        }
        return group;
    }

    @Override
    public Group closeVoteKick(String groupID, String groupLeaderID)
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
        if(group.getKickCandidate() == null)
        {
            log.info("User " + groupLeaderID + " has not started a vote kick");
            return null;
        }
        group.setKickCandidate(null);
        group.setKickVotes(new ArrayList<>());
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group acceptVoteKick(String groupID, String accountID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountID))
        {
            log.info("User " + accountID + " is not in group " + groupID);
            return null;
        }
        if(group.getKickCandidate() == null)
        {
            log.info("Group " + groupID + " has no ongoing vote kick");
            return null;
        }
        if(accountID.equals(group.getKickCandidate()))
        {
            log.info("User " + accountID + " is the one being vote kicked");
            return null;
        }
        if(accountID.equals(group.getGroupLeaderID()))
        {
            log.info("User " + accountID + " started the vote kick and cannot change vote");
            return null;
        }
        if(group.hasKickVote(accountID))
        {
            log.info("User " + accountID + " has already voted to kick " + group.getKickCandidate());
            return null;
        }
        group.addKickVote(accountID);
        groupRepository.save(group);
        if(group.getKickVotes().size()*2 >= group.getGroupMemberIDs().size() - 1)
        {
            group = this.removeGroupMember(groupID, group.getGroupLeaderID(), group.getKickCandidate());
            group.setKickCandidate(null);
            group.setKickVotes(new ArrayList<>());
            groupRepository.save(group);
        }
        return group;
    }

    @Override
    public Group denyVoteKick(String groupID, String accountID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return null;
        }
        if(!group.hasGroupMember(accountID))
        {
            log.info("User " + accountID + " is not in group " + groupID);
            return null;
        }
        if(group.getKickCandidate() == null)
        {
            log.info("Group " + groupID + " has no ongoing vote kick");
            return null;
        }
        if(accountID.equals(group.getKickCandidate()))
        {
            log.info("User " + accountID + " is the one being vote kicked");
            return null;
        }
        if(accountID.equals(group.getGroupLeaderID()))
        {
            log.info("User " + accountID + " started the vote kick and cannot change vote");
            return null;
        }
        if(!group.hasKickVote(accountID))
        {
            log.info("User " + accountID + " has already chosen to not kick " + group.getKickCandidate());
            return null;
        }
        group.removeKickVote(accountID);
        groupRepository.save(group);
        return group;
    }

    @Override
    public List<Group> searchBySettings(String username, List<Group> groups)
    {
        if(!accountRepository.existsByUsername(username))
        {
            log.info("User " + username + " not found");
            return null;
        }
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        Account user = accountRepository.findByUsername(username);
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            System.out.println(group.getGroupName());
            boolean exit = false;
            for(String member : group.getGroupMemberIDs().keySet())
            {
                if(member.equals(user.getAccountID()))
                {
                    System.out.println("is member");
                    exit = true;
                    break;
                }
            }
            if(exit)
            {
                continue;
            }
            else if(!group.isPublic())
            {
                System.out.println("is not public");
                continue;
            }
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
          
            if(leader == null)
            {
                continue;
            }
            else if(group.getReputationReq()*leader.getReputation() > user.getReputation() || user.getReputationReq() * user.getReputation() > leader.getReputation())
            {
                System.out.println("reputation issue");
                continue;
            }
//            else if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > user.getProximityReq())
//            {
//                System.out.println("proximity issue");
//                continue;
//            }
            qualified.add(group);
        }
        Comparator<Group> byGroupName = Comparator.comparing(Group::getGroupName);
        qualified.sort(byGroupName);
        return qualified;
    }

    @Override
    public List<Group> searchBySkillReqs(List<String> skillRequirements, List<Group> groups)
    {
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            List<Skill> skills = this.getAllSkillReqs(group.getGroupID());
            boolean exit = false;
            for(String skillName : skillRequirements)
            {
                int i;
                for(i = 0; i < skills.size(); i++)
                {
                    if(skills.get(i).getSkillName().equals(skillName))
                    {
                        break;
                    }
                }
                if(i == skills.size())
                {
                    exit = true;
                    break;
                }
            }
            if(!exit)
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByGroupName(String groupName, List<Group> groups)
    {
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            if(group.getGroupName().toLowerCase().equals(groupName.toLowerCase()))
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderFirstName(String firstName, List<Group> groups)
    {
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getFirstName().toLowerCase().equals(firstName.toLowerCase()))
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderLastName(String lastName, List<Group> groups)
    {
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getLastName().toLowerCase().equals(lastName.toLowerCase()))
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Group> searchByLeaderFullName(String firstName, String lastName, List<Group> groups)
    {
        if(groups == null)
        {
            groups = groupRepository.findAll();
        }
        List<Group> qualified = new ArrayList<>();
        for(Group group : groups)
        {
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
            if(leader.getFirstName().toLowerCase().equals(firstName.toLowerCase()) &&
                    leader.getLastName().toLowerCase().equals(lastName.toLowerCase()))
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Skill> getAllSkillReqs(String groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        List<Skill> skills = new ArrayList<>();
        for(String skillID : group.getSkillReqs().keySet())
        {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        Collections.sort(skills);
        return skills;
    }

    @Override
    public Skill getSkillReq(String groupID, String skillName)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        List<Skill> skills = this.getAllSkillReqs(groupID);
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
    public Group addSkillReq(String groupID, String groupLeaderID, String skillName, int skillLevel)
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
        group.addSkillReq(skill);
        groupRepository.save(group);
        return group;
    }

    @Override
    public Group removeSkillReq(String groupID, String groupLeaderID, String skillName)
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
    public boolean meetsGroupRequirements(String groupID, String accountID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return false;
        }
        if(!accountRepository.existsByAccountID(accountID))
        {
            log.info("User " + accountID + " not found");
            return false;
        }
        Group group = groupRepository.findByGroupID(groupID);
        Account user = accountRepository.findByAccountID(accountID);
        Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
        if(group.hasGroupMember(user.getAccountID()))
        {
            log.info("User " + accountID + " is already a member of group " + groupID);
            return false;
        }
//        if(group.getReputationReq()*leader.getReputation() > user.getReputation() || user.getReputationReq() * user.getReputation() > leader.getReputation())
//        {
//            return false;
//        }
//        if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > group.getProximityReq())
//        {
//            return false;
//        }
        boolean metReq = true;
        for(String skillReqID : group.getSkillReqs().keySet())
        {
            metReq = false;
            Skill skillReq = skillRepository.findBySkillID(skillReqID);
            for(String skillID : user.getSkillIDs().keySet())
            {
                Skill skill = skillRepository.findBySkillID(skillID);
                if(skill.getSkillName().equals(skillReq.getSkillName()))
                {
                    if(skill.getSkillLevel() >= skillReq.getSkillLevel())
                    {
                        System.out.println("metReq true");
                        metReq = true;
                    }
                    break;
                }
            }
            if(!metReq)
            {
                break;
            }
        }
        return metReq;
    }

    @Override
    public Message requestToJoinGroup(String groupID, String accountID)
    {
        if(!this.meetsGroupRequirements(groupID, accountID))
        {
            System.out.println("Does not meet requirements");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        Account user = accountRepository.findByAccountID(accountID);
        Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
        Message joinRequest = new Message(accountID,
                "User " + user.getFullName() + " wishes to join your group " + group.getGroupName(),
                Message.Types.JOIN_REQUEST);
        joinRequest.setGroupID(groupID);
        messageRepository.save(joinRequest);
        leader.addMessage(joinRequest);
        accountRepository.save(leader);
        return joinRequest;
    }

    @Override
    public Message acceptJoinRequest(String groupLeaderID, String messageID)
    {
        if(!accountRepository.existsByAccountID(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " not found");
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderID);
        if(!leader.hasMessage(messageID))
        {
            log.info("User " + groupLeaderID + " did not receive message " + messageID);
            return null;
        }
        Message request = messageRepository.findByMessageID(messageID);
        messageRepository.delete(request);
        leader.removeMessage(messageID);
        accountRepository.save(leader);
        if(!accountRepository.existsByAccountID(request.getSenderID()))
        {
            log.info("User " + request.getSenderID() + " not found");
            return null;
        }
        if(!groupRepository.existsByGroupID(request.getGroupID()))
        {
            log.info("Group " + request.getGroupID() + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(request.getGroupID());
        Account sender = accountRepository.findByAccountID(request.getSenderID());
        group.addGroupMember(sender);
        groupRepository.save(group);
        Message acceptance = new Message(groupLeaderID,
                "You have been accepted into group " + group.getGroupName(),
                Message.Types.GROUP_ACCEPTED);
        messageRepository.save(acceptance);
        sender.addMessage(acceptance);
        sender.addGroup(group);
        accountRepository.save(sender);
        return acceptance;
    }

    @Override
    public Message denyJoinRequest(String groupLeaderID, String messageID)
    {
        if(!accountRepository.existsByAccountID(groupLeaderID))
        {
            log.info("User " + groupLeaderID + " not found");
            return null;
        }
        Account leader = accountRepository.findByAccountID(groupLeaderID);
        if(!leader.hasMessage(messageID))
        {
            log.info("User " + groupLeaderID + " did not receive message " + messageID);
            return null;
        }
        Message request = messageRepository.findByMessageID(messageID);
        if (!accountRepository.existsByAccountID(request.getSenderID())) {
            log.info("User " + request.getSenderID() + " not found");
            return null;
        }
        Account sender = accountRepository.findByAccountID(request.getSenderID());
        sender.removeMessage(messageID);
        messageRepository.delete(request);
        leader.removeMessage(messageID);
        accountRepository.save(leader);
        accountRepository.save(sender);
        return request;
    }

    @Override
    public Message sendMessage(String groupID, String senderID, String receiverID, String content, int type)
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
        Message message = new Message(senderID, content, type);
        message.setGroupID(group.getGroupID());
        messageRepository.save(message);
        receiver.addMessage(message);
        accountRepository.save(receiver);
        return message;
    }

    @Override
    public void sendChatMessage(ChatMessage msg, String groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return;
        }
        Group group = groupRepository.findByGroupID(groupID);
        Account a = accountRepository.findByUsername(msg.getSenderId());
        if (a == null) {
            log.info("No accountID found for User: " + msg.getSenderId());
            return;
        }
        if(!group.hasGroupMember(a.getAccountID()))
        {
            log.info("User " + msg.getSenderId() + " is not in the group " + groupID);
            return;
        }

        group.addMessage(msg);
        groupRepository.save(group);
    }

    /* TODO: There is only one message for WebSockets to work properly */
    @Override
    public Message initiateRatings(String groupID, String groupLeaderID)
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
        if(!group.startMemberRatings())
        {
            log.info("Group " + groupID + " has maxed out the limit for group ratings");
            return null;
        }
        Message message = new Message(groupLeaderID, "You can now rate your fellow members in group " + group.getGroupName() + "!", Message.Types.RATE_REQUEST);
        message.setGroupID(groupID);
        messageRepository.save(message);
        for(String accountID : group.getGroupMemberIDs().keySet())
        {
            if(accountID.equals(groupLeaderID))
            {
                continue;
            }
            Account member = accountRepository.findByAccountID(accountID);
            member.addMessage(message);
            accountRepository.save(member);
        }
        groupRepository.save(group);
        return message;
    }

    @Override
    public Map<String, Integer> getGroupMemberRatingForm(String groupID, String rateeID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.hasGroupMember(rateeID))
        {
            log.info("User " + rateeID + " is not in group " + groupID);
            return null;
        }
        Account member = accountRepository.findByAccountID(rateeID);
        Map<String, Integer> form = new TreeMap<>();
        for(String skillID : group.getSkillReqs().keySet())
        {
            for(String id : member.getSkillIDs().keySet())
            {
                String skillReq = skillRepository.findBySkillID(skillID).getSkillName();
                String skillName = skillRepository.findBySkillID(id).getSkillName();
                if(skillReq.equals(skillName))
                {
                    form.put(skillName, 0);
                    break;
                }
            }
        }
        return form;
    }

    @Override
    public String rateGroupMember(String groupID, String raterID, String rateeID, boolean endorse, Map<String, Integer> skillRatings)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        if(!group.hasGroupMember(raterID))
        {
            log.info("User " + raterID + " is not in group " + groupID);
            return null;
        }
        if(!group.hasGroupMember(rateeID))
        {
            log.info("User " + raterID + " is not in group " + groupID);
            return null;
        }
        if(!group.canGiveRating(raterID, rateeID))
        {
            log.info("User " + raterID + " cannot rate user " + rateeID);
            return null;
        }
        Account member = accountRepository.findByAccountID(rateeID);
        List<Skill> skills = new ArrayList<>();
        List<Integer> ratings = new ArrayList<>();
        for(String skillName : skillRatings.keySet())
        {
            if(skillRatings.get(skillName) > 0 && skillRatings.get(skillName) <= 10)
            {
                for(Map.Entry<String, String> entry : member.getSkillIDs().entrySet())
                {
                    if(entry.getValue().equals(skillName))
                    {
                        skills.add(skillRepository.findBySkillID(entry.getKey()));
                        ratings.add(skillRatings.get(skillName));
                        break;
                    }
                }
            }
        }
        Map<String, Integer> updatedSkills = member.addSkillRatings(skills, ratings);
        for(Map.Entry<String, Integer> entry : updatedSkills.entrySet())
        {
            Skill skill = skillRepository.findBySkillNameAndSkillLevel(entry.getKey(), entry.getValue());
            member.addSkill(skill);
        }
        if(endorse)
        {
            Account rater = accountRepository.findByAccountID(raterID);
            int reputation = member.getReputation();
            reputation += (2 + rater.getReputation()/15);
            reputation = Math.min(reputation, 100);
            member.setReputation(reputation);
        }
        accountRepository.save(member);
        groupRepository.save(group);
        return "Success";
    }

    @Override
    public List<Account> broadcastEvent(String groupID, String groupLeaderID, String description, int proximity, List<String> skillNames)
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
        Account leader = accountRepository.findByAccountID(groupLeaderID);
        List<Account> accounts = accountRepository.findAll();
        List<Account> qualified = new ArrayList<>();
        for(Account account : accounts)
        {
            if(group.getGroupMemberIDs().containsKey(account.getAccountID()))
            {
                continue;
            }
            if(account.distanceTo(leader.getLatitude(), leader.getLongitude()) > proximity)
            {
                continue;
            }
            boolean exit = false;
            for(String skillName : skillNames)
            {
                if(!account.getSkillIDs().containsValue(skillName))
                {
                    exit = true;
                    break;
                }
            }
            if(exit)
            {
                continue;
            }
            Message invite = new Message(groupLeaderID,
                    "You have been invited to an event hosted by group " + group.getGroupName() + "! Here are the details: " + description,
                    Message.Types.EVENT_INVITE);
            invite.setGroupID(groupID);
            account.addMessage(invite);
            accountRepository.save(account);
            qualified.add(account);
        }
        return qualified;
    }
}
