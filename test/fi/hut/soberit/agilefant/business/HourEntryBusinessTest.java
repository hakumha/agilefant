package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailySpentEffort;



public class HourEntryBusinessTest {

    private HourEntryBusinessImpl hourEntryBusiness;
    private BacklogHourEntryDAO backlogHourEntryDAO;
    private HourEntryDAO hourEntryDAO;
    private StoryBusiness storyBusiness;
    private UserBusiness userBusiness;
    
    private Collection<User> targetUsers;
    private Set<Integer> targetUserIds;
    
    private void compareHe(HourEntry he1, HourEntry he2) {

        assertEquals(he1.getDate(), he2.getDate());
        assertEquals(he1.getDescription(), he2.getDescription());
        assertEquals(he1.getMinutesSpent(), he2.getMinutesSpent());
    }
    
    @Before
    public void setUp_dependencies() {
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryDAO = createMock(HourEntryDAO.class);
        backlogHourEntryDAO = createMock(BacklogHourEntryDAO.class);
        
        storyBusiness = createMock(StoryBusiness.class);
        hourEntryBusiness.setStoryBusiness(storyBusiness);
                
        userBusiness = createMock(UserBusiness.class);
        hourEntryBusiness.setUserBusiness(userBusiness);
        
        hourEntryBusiness.setHourEntryDAO(hourEntryDAO);
        hourEntryBusiness.setBacklogHourEntryDAO(backlogHourEntryDAO);
    }
    
    @Before
    public void setUp_data() {
        targetUsers = Arrays.asList(new User());
        targetUserIds = new HashSet<Integer>(Arrays.asList(1));
    }
    
    private void replayAll() {
        replay(hourEntryDAO, backlogHourEntryDAO, storyBusiness, userBusiness);
    }
    
    private void verifyAll() {
        verify(hourEntryDAO, backlogHourEntryDAO, storyBusiness, userBusiness);
    }
    
    @Test
    public void testCalculateSumOfBacklogsHourEntries() {
        Iteration iteration = new Iteration();
        iteration.setId(123);
        expect(hourEntryDAO.calculateIterationHourEntriesSum(123))
            .andReturn(22332L);
        replay(hourEntryDAO);
        
        assertEquals(22332L, hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration));
        
        verify(hourEntryDAO);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateSumOfBacklogsHourEntries_nullBacklog() {
        hourEntryBusiness.calculateSumOfIterationsHourEntries(null);
    }
    
    @Test
    public void testCalculateSumByUserAndTimeInterval() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(7);
        User user = new User();
        user.setId(11);
        
        expect(hourEntryDAO.calculateSumByUserAndTimeInterval(user, start, end)).andReturn(400L);
        expect(hourEntryDAO.calculateSumByUserAndTimeInterval(11, start, end)).andReturn(400L);
        
        replay(hourEntryDAO);
        assertEquals(400L, this.hourEntryBusiness.calculateSumByUserAndTimeInterval(11, start, end));
        assertEquals(400L, this.hourEntryBusiness.calculateSumByUserAndTimeInterval(user, start, end));
        verify(hourEntryDAO);
    }
    
    @Test
    public void testGetEntriesByUserAndTimeInterval() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(7);
        List<HourEntry> noEntries = Collections.emptyList();
        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 11)).andReturn(noEntries);
        replay(hourEntryDAO);
        assertEquals(noEntries, hourEntryBusiness.getEntriesByUserAndTimeInterval(11, start, end));
        verify(hourEntryDAO);
    }
    
    private static HourEntry createEntry(int year, int month, int day, long effort) {
        HourEntry entry = new HourEntry();
        DateTime date = new DateTime(year, month, day, 0,0,0,0);
        entry.setMinutesSpent(effort);
        entry.setDate(date);
        return entry;
    }
    @Test
    public void testGetDailySpentEffortByWeek() {
        DateTime start = new DateTime(2009,6,1,0,0,1,0);
        DateTime end = new DateTime(2009,6,7,23,59,59,0);
        List<HourEntry> entries = Collections.emptyList();
        
        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);

        replay(hourEntryDAO);
        assertEquals(7, hourEntryBusiness.getDailySpentEffortByWeek(start.toLocalDate(), 0).size());
        verify(hourEntryDAO);
    }
    
    @Test
    public void calculateWeekSum() {
        DateTime start = new DateTime(2009,6,1,0,0,1,0);
        DateTime end = new DateTime(2009,6,7,23,59,59,0);
        
        expect(hourEntryDAO.calculateSumByUserAndTimeInterval(0, start, end)).andReturn(0L);

        replay(hourEntryDAO);
        assertEquals(0L, hourEntryBusiness.calculateWeekSum(start.plusDays(3).toLocalDate(), 0));
        verify(hourEntryDAO);
    }
    
    @Test
    public void testGetDailySpentEffortByInterval_noData() {
        DateTime start = new DateTime(2009,6,1,0,0,0,0);
        DateTime end = new DateTime(2009,6,7,0,0,0,0);
        
        List<HourEntry> entries = new ArrayList<HourEntry>();

        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);

        replay(hourEntryDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
        assertEquals(7, res.size());
        assertEquals(null, res.get(0).getSpentEffort());
        assertEquals(null, res.get(1).getSpentEffort());
        assertEquals(null, res.get(2).getSpentEffort());
        assertEquals(null, res.get(3).getSpentEffort());
        assertEquals(null, res.get(4).getSpentEffort());
        assertEquals(null, res.get(5).getSpentEffort());
        assertEquals(null, res.get(6).getSpentEffort());
        
        verify(hourEntryDAO);
    }
    
    @Test
    public void testGetDailySpentEffortByInterval_yearChanges() {
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2008, 1, 28, 100));
        entries.add(createEntry(2008, 12, 28, 900));
        entries.add(createEntry(2008, 12, 28, 1000));
        entries.add(createEntry(2008, 12, 29, 4000));
        entries.add(createEntry(2009, 1, 1, 50000));
        entries.add(createEntry(2009, 1, 2, 6000000));
        entries.add(createEntry(2009, 7, 28, 70000000));
        DateTime start = new DateTime(2008,12,27,0,0,0,0);
        DateTime end = new DateTime(2009,1,3,0,0,0,0);
        
        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);
        replay(hourEntryDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
        assertEquals(8, res.size());
        assertEquals(null, res.get(0).getSpentEffort());
        assertEquals(1900L, (long)res.get(1).getSpentEffort());
        assertEquals(4000L, (long)res.get(2).getSpentEffort());
        assertEquals(null, res.get(3).getSpentEffort());
        assertEquals(null, res.get(4).getSpentEffort());
        assertEquals(50000L, (long)res.get(5).getSpentEffort());
        assertEquals(6000000L, (long)res.get(6).getSpentEffort());
        assertEquals(null, res.get(7).getSpentEffort());
        verify(hourEntryDAO);
    }
    
    @Test
    public void testGetDailySpentEffortByInterval() {
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2009, 1, 28, 100));
        entries.add(createEntry(2009, 4, 28, 900));
        entries.add(createEntry(2009, 4, 28, 1000));
        entries.add(createEntry(2009, 4, 30, 4000));
        entries.add(createEntry(2009, 4, 30, 50000));
        entries.add(createEntry(2009, 5, 1, 6000000));
        entries.add(createEntry(2009, 5, 28, 70000000));
        DateTime start = new DateTime(2009,4,28,0,0,0,0);
        DateTime end = new DateTime(2009,5,1,23,59,0,0);

        
        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);
        replay(hourEntryDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
        assertEquals(4, res.size());
        assertEquals(1900L, (long)res.get(0).getSpentEffort());
        assertEquals(null, res.get(1).getSpentEffort());
        assertEquals(54000L, (long)res.get(2).getSpentEffort());
        assertEquals(6000000L, (long)res.get(3).getSpentEffort());
        verify(hourEntryDAO);
    }
    
    @Test
    public void testGetEntriesByUserAndDay() {
        DateTime start = new DateTime(2009,6,2,0,0,0,0);
        DateTime end = new DateTime(2009,6,2,23,59,59,0);
        expect(hourEntryDAO.getHourEntriesByFilter(start, end, 42)).andReturn(null);
        replay(hourEntryDAO);
        assertEquals(null, hourEntryBusiness.getEntriesByUserAndDay(start.toLocalDate(), 42));
        verify(hourEntryDAO);
    }
    
    @Test
    public void testLogStoryEffort() {
        HourEntry effortEntry = new HourEntry();
        effortEntry.setDate(new DateTime());
        effortEntry.setDescription("daadaa");
        effortEntry.setMinutesSpent(10L);
        
        Story parent = new Story();
        
        expect(storyBusiness.retrieve(1)).andReturn(parent);
        expect(userBusiness.retrieveMultiple(targetUserIds)).andReturn(targetUsers);

        Capture<StoryHourEntry> storedEntry = new Capture<StoryHourEntry>();
        expect(hourEntryDAO.create(EasyMock.capture(storedEntry))).andReturn(1);
        
        replayAll();
        hourEntryBusiness.logStoryEffort(1, effortEntry, targetUserIds);
        verifyAll();
        StoryHourEntry actual = (StoryHourEntry)storedEntry.getValue();
        assertEquals(parent, actual.getStory());
        compareHe(effortEntry, actual);
    }
    
    @Test(expected=ObjectNotFoundException.class)
    public void testLogStoryEffort_invalidStory() {
        HourEntry effortEntry = new HourEntry();
                
        expect(storyBusiness.retrieve(1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        hourEntryBusiness.logStoryEffort(1, effortEntry, targetUserIds);
        verifyAll();

    }
    
    @Test(expected=ObjectNotFoundException.class)
    public void testLogStoryEffort_invalidUser() {
        HourEntry effortEntry = new HourEntry();
        
        Story parent = new Story();
        
        expect(storyBusiness.retrieve(1)).andReturn(parent);
        expect(userBusiness.retrieveMultiple(targetUserIds)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        hourEntryBusiness.logStoryEffort(1, effortEntry, targetUserIds);
        verifyAll();

    }
    
    @Test
    public void testLogTaskEffort() {
        replayAll();
        verifyAll();
    }
    @Test
    public void testLogBacklogEffort() {
        replayAll();
        verifyAll();
    }

}
