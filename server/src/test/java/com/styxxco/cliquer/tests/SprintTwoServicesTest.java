package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.database.*;
import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.domain.Group;
import com.styxxco.cliquer.domain.Message;
import com.styxxco.cliquer.domain.Message.Types;
import com.styxxco.cliquer.domain.Skill;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.GroupService;
import com.styxxco.cliquer.service.impl.GroupServiceImpl;
import org.bson.types.ObjectId;
import com.styxxco.cliquer.service.impl.AccountServiceImpl;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SprintTwoServicesTest {

    @Autowired
    public AccountRepository accountRepository;
    @Autowired
    public SkillRepository skillRepository;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public GroupRepository groupRepository;

    /* Function to clear items that should not already be in database */
    public void clearDatabase()
    {
        if(accountRepository.existsByUsername("reed226"))
        {
            accountRepository.delete(accountRepository.findByUsername("reed226"));
        }
        if(accountRepository.existsByUsername("montgo38"))
        {
            accountRepository.delete(accountRepository.findByUsername("montgo38"));
        }
        if(accountRepository.existsByUsername("knagar"))
        {
            accountRepository.delete(accountRepository.findByUsername("knagar"));
        }
        if(accountRepository.existsByUsername("buckmast"))
        {
            accountRepository.delete(accountRepository.findByUsername("buckmast"));
        }
        if(accountRepository.existsByUsername("rbuckmas"))
        {
            accountRepository.delete(accountRepository.findByUsername("rbuckmas"));
        }

        if(skillRepository.existsBySkillName("Programming"))
        {
            skillRepository.delete(skillRepository.findBySkillName("Programming"));
        }
        if(skillRepository.existsBySkillName("Lifter"))
        {
            skillRepository.delete(skillRepository.findBySkillName("Lifter"));
        }
        if(skillRepository.existsBySkillName("Board Gaming"))
        {
            skillRepository.delete(skillRepository.findBySkillName("Board Gaming"));
        }
    }

    /* Test group searching filters and filter chaining */
    @Test
    public void testGroupSearching() {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        Group hula = groupService.createGroup(
                "Hoops",
                "To play hula hoops",
                shawn.getAccountID());
        Group hoops = groupService.createGroup(
                "Hoops",
                "To play basketball",
                shawn.getAccountID());
        Group games = groupService.createGroup(
                "Hoops",
                "To play basketball games",
                shawn.getAccountID());
        Group styxx = groupService.createGroup(
                "Styxx",
                "To make an app",
                shawn.getAccountID());

        jordan.setReputation(50);
        jordan.setReputationReq(0.5);
        shawn.setReputation(60);
        cliquer.setReputationReq(1.0);
        hoops.setReputationReq(0.25);
        hoops.setReputationReq(0.5);
        games.setReputationReq(0.5);
        styxx.setReputationReq(0.6);

        cliquer.setPublic(true);
        hula.setPublic(true);
        hoops.setPublic(true);
        games.setPublic(false);
        styxx.setPublic(true);

        accountRepository.save(jordan);
        accountRepository.save(shawn);
        groupRepository.save(cliquer);
        groupRepository.save(hula);
        groupRepository.save(hoops);
        groupRepository.save(games);
        groupRepository.save(styxx);

        Skill lifter = accountService.addSkillToDatabase("Lifter");
        groupService.addSkillReq(hula.getGroupID(), shawn.getAccountID(), "Lifter", 2);
        groupService.addSkillReq(hoops.getGroupID(), shawn.getAccountID(), "Lifter", 6);

        List<Group> groups = groupService.searchByGroupName("Hoops", null);
        assertEquals(3, groups.size());
        assertEquals("Hoops", groups.get(0).getGroupName());

        List<String> reqs = new ArrayList<>();
        reqs.add("Lifter");
        groups = groupService.searchBySkillReqs(reqs, groups);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).getSkillReqs().size());

        groups = groupService.searchBySettings("reed226", groups);
        assertEquals(1, groups.size());
        assertEquals("To play basketball", groups.get(0).getGroupPurpose());

        groups = groupService.searchBySettings("reed226", null);
        assertEquals(2, groups.size());
        assertEquals("Hoops", groups.get(0).getGroupName());

        List<Group> first = groupService.searchByLeaderFirstName("Shawn", null);
        List<Group> second = groupService.searchByLeaderLastName("Montgomery", null);
        List<Group> third = groupService.searchByLeaderFullName("Shawn", "Montgomery", null);
        assertEquals(first.size(), second.size());
        assertEquals(first.size(), third.size());

        hula = groupRepository.findByGroupID(hula.getGroupID());
        hoops = groupRepository.findByGroupID(hoops.getGroupID());

        skillRepository.delete(lifter);
        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        for(ObjectId id : hula.getSkillReqs())
        {
            skillRepository.delete(id.toString());
        }
        for(ObjectId id : hoops.getSkillReqs())
        {
            skillRepository.delete(id.toString());
        }
        groupRepository.delete(cliquer);
        groupRepository.delete(hula);
        groupRepository.delete(hoops);
        groupRepository.delete(games);
        groupRepository.delete(styxx);
    }

    @Test
    public void testFriendInvites()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "montgo38@purdue.edu", "Kevin", "Nagar");

        Message invite = accountService.sendFriendInvite("reed226", shawn.getAccountID());
        assertEquals(jordan.getAccountID(), invite.getSenderID());
        shawn = accountRepository.findByUsername(shawn.getUsername());
        assertEquals(invite.getMessageID(), shawn.getMessageIDs().get(0));
        assertEquals(Types.FRIEND_INVITE, invite.getType());
        ObjectId first = invite.getMessageID();

        Account account = accountService.acceptFriendInvite("montgo38", invite.getMessageID());
        assertEquals(jordan.getFirstName(), account.getFirstName());
        shawn = accountRepository.findByUsername(shawn.getUsername());
        assertEquals(jordan.getAccountID(), shawn.getFriendIDs().get(0));
        jordan = accountRepository.findByUsername(jordan.getUsername());
        assertEquals(shawn.getAccountID(), jordan.getFriendIDs().get(0));
        assertEquals(0, shawn.getMessageIDs().size());

        invite = accountService.sendFriendInvite("reed226", kevin.getAccountID());
        assertEquals(jordan.getAccountID(), invite.getSenderID());
        kevin = accountRepository.findByUsername(kevin.getUsername());
        assertEquals(invite.getMessageID(), kevin.getMessageIDs().get(0));
        assertEquals(Types.FRIEND_INVITE, invite.getType());
        ObjectId second = invite.getMessageID();

        String result = accountService.rejectFriendInvite("knagar", invite.getMessageID());
        assertEquals("Success", result);
        kevin = accountRepository.findByUsername(shawn.getUsername());
        assertEquals(0, kevin.getFriendIDs().size());
        jordan = accountRepository.findByUsername(jordan.getUsername());
        assertEquals(1, jordan.getFriendIDs().size());
        assertEquals(0, kevin.getMessageIDs().size());

        invite = accountService.sendFriendInvite("reed226", shawn.getAccountID());
        assertNull(invite);
        assertEquals(false, messageRepository.existsByMessageID(first));
        assertEquals(false, messageRepository.existsByMessageID(second));

        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
    }
}
