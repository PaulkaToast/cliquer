package com.styxxco.cliquer.service.impl;

import com.styxxco.cliquer.database.AccountRepository;
import com.styxxco.cliquer.database.GroupRepository;
import com.styxxco.cliquer.database.MessageRepository;
import com.styxxco.cliquer.database.SkillRepository;
import com.styxxco.cliquer.domain.*;
import com.styxxco.cliquer.service.GroupService;
import lombok.extern.log4j.Log4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        Account user = accountRepository.findByAccountID(groupLeaderID);
        Group group = new Group(groupName, groupPurpose, groupLeaderID);
        group.setOwnerUID(user.getUsername());
        this.groupRepository.save(group);
        user.addGroup(group.getGroupID());
        this.accountRepository.save(user);
        return group;
    }

    @Override
    public Group deleteGroup(ObjectId groupID, ObjectId groupLeaderID)
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
        return group;

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
            log.info("Group " + groupID + " is a private group");
            return null;
        }
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
        Message notification = new Message(groupID,
                "You have been removed from the group " + group.getGroupName(),
                Message.Types.GROUP_NOTIFICATION);
        messageRepository.save(notification);
        member.addMessage(notification.getMessageID());
        member.removeGroup(groupID);
        accountRepository.save(member);
        return group;
    }

    @Override
    public Group startVoteKick(ObjectId groupID, ObjectId groupLeaderID, ObjectId accountID)
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
        if(!this.hasGroupMember(group, accountID))
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
    public Group closeVoteKick(ObjectId groupID, ObjectId groupLeaderID)
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
    public Group acceptVoteKick(ObjectId groupID, ObjectId accountID)
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
        if(!this.hasGroupMember(group, accountID))
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
    public Group denyVoteKick(ObjectId groupID, ObjectId accountID)
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
        if(!this.hasGroupMember(group, accountID))
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
            boolean exit = false;
            for(ObjectId member : group.getGroupMemberIDs())
            {
                if(member.equals(user.getAccountID()))
                {
                    exit = true;
                }
            }
            if(exit)
            {
                continue;
            }
            else if(!group.isPublic())
            {
                continue;
            }
            Account leader = accountRepository.findByAccountID(group.getGroupLeaderID());
          
            if(leader == null)
            {
                continue;
            }
            else if(group.getReputationReq()*leader.getReputation() < user.getReputationReq()*user.getReputation())
            {
                continue;
            }
            else if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > user.getProximityReq())
            {
                continue;
            }
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
            if(group.getGroupName().equals(groupName))
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
            if(leader.getFirstName().equals(firstName))
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
            if(leader.getLastName().equals(lastName))
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
            if(leader.getFirstName().equals(firstName) && leader.getLastName().equals(lastName))
            {
                qualified.add(group);
            }
        }
        return qualified;
    }

    @Override
    public List<Skill> getAllSkillReqs(ObjectId groupID)
    {
        if(!groupRepository.existsByGroupID(groupID))
        {
            log.info("Group " + groupID + " not found");
            return null;
        }
        Group group = groupRepository.findByGroupID(groupID);
        List<Skill> skills = new ArrayList<>();
        for(ObjectId skillID : group.getSkillReqs())
        {
            Skill skill = skillRepository.findBySkillID(skillID);
            skills.add(skill);
        }
        Collections.sort(skills);
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
    public boolean meetsGroupRequirements(ObjectId groupID, ObjectId accountID)
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
        if(user.getAdjustedReputation() < group.getReputationReq() * leader.getReputation())
        {
            return false;
        }
        if(user.distanceTo(leader.getLatitude(), leader.getLongitude()) > group.getProximityReq())
        {
            return false;
        }
        boolean metReq = true;
        for(ObjectId skillReqID : group.getSkillReqs())
        {
            metReq = false;
            Skill skillReq = skillRepository.findBySkillID(skillReqID);
            for(ObjectId skillID : user.getSkillIDs())
            {
                Skill skill = skillRepository.findBySkillID(skillID);
                if(skill.getSkillName().equals(skillReq.getSkillName()))
                {
                    if(skill.getSkillLevel() > skillReq.getSkillLevel())
                    {
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
    public Message requestToJoinGroup(ObjectId groupID, ObjectId accountID)
    {
        if(!this.meetsGroupRequirements(groupID, accountID))
        {
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
        leader.addMessage(joinRequest.getMessageID());
        accountRepository.save(leader);
        return joinRequest;
    }

    @Override
    public Message acceptJoinRequest(ObjectId groupLeaderID, ObjectId messageID)
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
        group.addGroupMember(request.getSenderID());
        groupRepository.save(group);
        Message acceptance = new Message(request.getGroupID(),
                "You have been accepted into group " + group.getGroupName(),
                Message.Types.PROFILE_NOTIFICATION);
        messageRepository.save(acceptance);
        sender.addMessage(acceptance.getMessageID());
        sender.addGroup(group.getGroupID());
        accountRepository.save(sender);
        return acceptance;
    }

    @Override
    public Message denyJoinRequest(ObjectId groupLeaderID, ObjectId messageID)
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
        Message denial = new Message(request.getGroupID(),
                "You have been rejected from joining group " + group.getGroupName(),
                Message.Types.PROFILE_NOTIFICATION);
        messageRepository.save(denial);
        sender.addMessage(denial.getMessageID());
        accountRepository.save(sender);
        return denial;
    }

    @Override
    public Message sendMessage(ObjectId groupID, ObjectId senderID, ObjectId receiverID, String content, int type)
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

    @Override
    public void sendChatMessage(ChatMessage msg, ObjectId groupID)
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
        }
        if(!group.getGroupMemberIDs().contains(a.getAccountID()))
        {
            log.info("User " + msg.getSenderId() + " is not in the group " + groupID);
            return;
        }

        group.addMessage(msg);
        groupRepository.save(group);
    }
}
