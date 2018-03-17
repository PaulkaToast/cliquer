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

    /* Back end Unit Test for User Story 10 */
    @Test
    public void testGroupSearchFilters() {
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
        jordan.setLatitude(40.00);
        jordan.setLongitude(-80.00);
        jordan.setProximityReq(Integer.MAX_VALUE);
        shawn.setReputation(60);
        shawn.setLatitude(40.2);
        shawn.setLongitude(-80.4);
        shawn.setProximityReq(Integer.MAX_VALUE);
        cliquer.setReputationReq(0.5);
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

        groups = groupService.searchBySettings("montgo38", null);
        assertEquals(1, groups.size());
        assertEquals("Cliquer", groups.get(0).getGroupName());

        assertEquals(25, jordan.distanceTo(shawn.getLatitude(), shawn.getLongitude()));
        jordan.setProximityReq(30);
        shawn.setProximityReq(20);

        accountRepository.save(jordan);
        accountRepository.save(shawn);

        groups = groupService.searchBySettings("reed226", null);
        assertEquals(2, groups.size());
        assertEquals("Hoops", groups.get(0).getGroupName());

        groups = groupService.searchBySettings("montgo38", null);
        assertEquals(0, groups.size());

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

    /* Back end Unit Test for User Story 13 */
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
        kevin = accountRepository.findByUsername(kevin.getUsername());
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

    /* Back end Unit Test for User Story 15 */
    @Test
    public void testReputationSuggestions()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "montgo38@purdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");
        Account rhys = accountService.createAccount("rbuckmas", "rbuckmas@purdue.edu", "Rhys", "Buckmaster");

        jordan.setReputation(55);
        shawn.setReputation(59);
        kevin.setReputation(49);
        buckmaster.setReputation(69);
        rhys.setReputation(45);

        accountRepository.save(jordan);
        accountRepository.save(shawn);
        accountRepository.save(kevin);
        accountRepository.save(buckmaster);
        accountRepository.save(rhys);

        List<Account> results = accountService.searchByReputation(70, true, false);
        assertEquals(true, results.isEmpty());

        results = accountService.searchByReputation(69, true, false);
        assertEquals(1, results.size());
        assertEquals("Jordan Buckmaster", results.get(0).getFullName());

        for(int i = 0; i < 10; i++)
        {
            results = accountService.searchByReputation(45, true, false);
            assertEquals(6, results.size());
            assertNull(results.get(1));
            assertEquals(true, (results.get(0).getReputation() <= 55));
            assertEquals("Jordan Buckmaster", results.get(2).getFullName());
            assertEquals("Shawn Montgomery", results.get(3).getFullName());
        }

        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
        accountRepository.delete(buckmaster);
        accountRepository.delete(rhys);
    }

    /* Back end Unit Test for User Story 16 */
    @Test
    public void testOptingOutOfSearch()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");
        Account rhys = accountService.createAccount("rbuckmas", "rbuckmas@purdue.edu", "Rhys", "Buckmaster");

        jordan.setReputation(55);
        jordan.setOptedOut(true);
        buckmaster.setReputation(45);
        rhys.setReputation(59);

        accountRepository.save(jordan);
        accountRepository.save(buckmaster);
        accountRepository.save(rhys);

        List<Account> results = accountService.searchByFirstName("Jordan");
        assertEquals(1, results.size());
        assertEquals("Buckmaster", results.get(0).getLastName());

        results = accountService.searchByFullName("Jordan Reed");
        assertEquals(0, results.size());

        results = accountService.searchByLastName("Reed");
        assertEquals(0, results.size());

        results = accountService.searchByReputation(50, false, false);
        assertEquals(1, results.size());
        assertEquals("Rhys Buckmaster", results.get(0).getFullName());

        accountRepository.delete(jordan);
        accountRepository.delete(buckmaster);
        accountRepository.delete(rhys);
    }

    /* Back end Unit Test for User Story 17 */
    @Test
    public void testReputationRange()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");

        jordan.setReputation(40);
        shawn.setReputation(80);
        shawn.setReputationReq(0.75);
        buckmaster.setReputation(60);
        buckmaster.setReputationReq(1.0);

        accountRepository.save(jordan);
        accountRepository.save(shawn);
        accountRepository.save(buckmaster);

        List<Account> results = accountService.searchByReputation(60, false, false);
        assertEquals(2, results.size());
        assertEquals("Montgomery", results.get(0).getLastName());
        assertEquals("Buckmaster", results.get(1).getLastName());

        results = accountService.searchByReputation(0, false, false);
        assertEquals(1, results.size());
        assertEquals("Reed", results.get(0).getLastName());

        results = accountService.searchByReputation(59, false, false);
        assertEquals(0, results.size());

        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(buckmaster);
    }

    /* Back end Unit Test for User Story 20 */
    @Test
    public void testNewUserFlag()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");

        jordan.setReputation(40);
        jordan.setLoggedInTime(Account.NEW_USER_HOURS*30);
        shawn.setReputation(80);
        shawn.setNewUser(false);
        kevin.setReputation(40);
        kevin.setLoggedInTime(Account.NEW_USER_HOURS*45);
        buckmaster.setReputation(60);
        buckmaster.setLoggedInTime(Account.NEW_USER_HOURS*60 + 1);

        accountRepository.save(jordan);
        accountRepository.save(shawn);
        accountRepository.save(kevin);
        accountRepository.save(buckmaster);

        String result = accountService.checkNewUserFlag("reed226");
        assertEquals("New User", result);

        result = accountService.checkNewUserFlag("montgo38");
        assertEquals("Experienced User", result);

        result = accountService.checkNewUserFlag("knagar");
        assertEquals("New User", result);

        result = accountService.checkNewUserFlag("buckmast");
        assertEquals("Experienced User", result);

        jordan = accountRepository.findByUsername(jordan.getUsername());
        shawn = accountRepository.findByUsername(shawn.getUsername());
        kevin = accountRepository.findByUsername(kevin.getUsername());
        buckmaster = accountRepository.findByUsername(buckmaster.getUsername());

        assertEquals(65, jordan.getAdjustedReputation());
        assertEquals(80, shawn.getAdjustedReputation());
        assertEquals(52, kevin.getAdjustedReputation());
        assertEquals(60, buckmaster.getAdjustedReputation());

        List<Account> accounts = accountService.searchByReputation(60, false, true);
        assertEquals(3, accounts.size());
        assertEquals("Reed", accounts.get(2).getLastName());

        accounts = accountService.searchByReputation(52, false, true);
        assertEquals(4, accounts.size());
        assertEquals("Nagar", accounts.get(2).getLastName());

        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
        accountRepository.delete(buckmaster);
    }

    /* Back end Unit Test for User Story 23 */
    @Test
    public void testPublicPrivateGroupSearch()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        Group hoops = groupService.createGroup(
                "Hoops",
                "To play basketball",
                shawn.getAccountID());
        Group games = groupService.createGroup(
                "Games",
                "To play video games",
                shawn.getAccountID());

        jordan.setLatitude(40.00);
        jordan.setLongitude(-80.00);
        jordan.setProximityReq(Integer.MAX_VALUE);
        shawn.setLatitude(40.2);
        shawn.setLongitude(-80.4);
        shawn.setProximityReq(Integer.MAX_VALUE);
        kevin.setLatitude(40.4);
        kevin.setLongitude(-80.8);
        kevin.setProximityReq(Integer.MAX_VALUE);

        accountRepository.save(jordan);
        accountRepository.save(shawn);
        accountRepository.save(kevin);

        cliquer = groupService.updateGroupSettings(cliquer.getGroupID(), cliquer.getGroupLeaderID(), "isPublic", "false");
        hoops = groupService.updateGroupSettings(hoops.getGroupID(), hoops.getGroupLeaderID(), "isPublic", "true");
        games = groupService.updateGroupSettings(games.getGroupID(), games.getGroupLeaderID(), "isPublic", "true");

        List<Group> groups = groupService.searchBySettings("knagar", null);
        assertEquals(2, groups.size());
        assertEquals("Games", groups.get(0).getGroupName());
        assertEquals("Hoops", groups.get(1).getGroupName());

        groupRepository.delete(cliquer);
        groupRepository.delete(hoops);
        groupRepository.delete(games);
        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
    }

    /* Back end Unit Test for User Story 24 */
    @Test
    public void testGroupClosing()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());

        Group hoops = groupService.createGroup(
                "Hoops",
                "Play ball",
                kevin.getAccountID());
        groupService.addGroupMember(hoops.getGroupID(), kevin.getAccountID(), jordan.getAccountID());

        String result = groupService.deleteGroup(cliquer.getGroupID(), jordan.getAccountID());
        assertNotNull(result);
        Group retrieve = groupService.getUserGroup(cliquer.getGroupID(), jordan.getAccountID());
        assertNull(retrieve);

        Account account = accountRepository.findByUsername(shawn.getUsername());
        assertEquals(0, account.getGroupIDs().size());
        account = accountRepository.findByUsername(kevin.getUsername());
        assertEquals(1, account.getGroupIDs().size());

        groupRepository.delete(cliquer);
        groupRepository.delete(hoops);
        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
    }

    /* Back end Unit Test for User Story 25 */
    @Test
    public void testGroupMemberKicking()
    {
        this.clearDatabase();
        AccountService accountService = new AccountServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);
        GroupService groupService = new GroupServiceImpl(accountRepository, skillRepository, messageRepository, groupRepository);

        Account jordan = accountService.createAccount("reed226", "reed226@purdue.edu", "Jordan", "Reed");
        Account shawn = accountService.createAccount("montgo38", "montgo38@purdue.edu", "Shawn", "Montgomery");
        Account kevin = accountService.createAccount("knagar", "knagar@purdue.edu", "Kevin", "Nagar");
        Account buckmaster = accountService.createAccount("buckmast", "buckmast@purdue.edu", "Jordan", "Buckmaster");
        Account rhys = accountService.createAccount("rbuckmas", "rbuckmas@purdue.edu", "Rhys", "Buckmaster");

        Group cliquer = groupService.createGroup(
                "Cliquer",
                "To create a web app that facilitates the teaming of people who may have never met before",
                jordan.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), shawn.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), buckmaster.getAccountID());
        groupService.addGroupMember(cliquer.getGroupID(), jordan.getAccountID(), rhys.getAccountID());

        Group result = groupService.acceptVoteKick(cliquer.getGroupID(), buckmaster.getAccountID());
        assertNull(result);

        result = groupService.startVoteKick(cliquer.getGroupID(), jordan.getAccountID(), kevin.getAccountID());
        assertEquals(kevin.getAccountID(), result.getKickCandidate());

        result = groupService.acceptVoteKick(cliquer.getGroupID(), kevin.getAccountID());
        assertNull(result);

        result = groupService.acceptVoteKick(cliquer.getGroupID(), buckmaster.getAccountID());
        assertEquals(false, result.getGroupMemberIDs().contains(kevin.getAccountID()));
        kevin = accountRepository.findByUsername(kevin.getUsername());
        assertEquals(0, kevin.getGroupIDs().size());

        result = groupService.acceptVoteKick(cliquer.getGroupID(), rhys.getAccountID());
        assertNull(result);

        result = groupService.startVoteKick(cliquer.getGroupID(), jordan.getAccountID(), rhys.getAccountID());
        assertEquals(rhys.getAccountID(), result.getKickCandidate());

        result = groupService.closeVoteKick(cliquer.getGroupID(), jordan.getAccountID());
        assertNull(result.getKickCandidate());

        result = groupService.removeGroupMember(cliquer.getGroupID(), jordan.getAccountID(), rhys.getAccountID());
        assertEquals(false, result.getGroupMemberIDs().contains(rhys.getAccountID()));

        groupRepository.delete(cliquer);
        accountRepository.delete(jordan);
        accountRepository.delete(shawn);
        accountRepository.delete(kevin);
        accountRepository.delete(buckmaster);
        accountRepository.delete(rhys);
    }
}
