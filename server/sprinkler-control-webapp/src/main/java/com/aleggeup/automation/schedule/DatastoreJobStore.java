/**
 * DatastoreJobStore.java
 *
 * Copyright 2015 [A Legge Up Consulting]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aleggeup.automation.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.JobStore;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.spi.TriggerFiredBundle;
import org.quartz.spi.TriggerFiredResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleggeup.automation.odm.DatastoreCollection;
import com.aleggeup.automation.odm.DatastoreCollectionRegistry;
import com.aleggeup.automation.schedule.quartz.model.CalendarWrapper;
import com.aleggeup.automation.schedule.quartz.model.JobDetailWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper;
import com.aleggeup.automation.schedule.quartz.model.OperableTriggerWrapper.OperableTriggerState;
import com.google.inject.Inject;

/**
 * @author Stephen Legge
 */
public class DatastoreJobStore implements JobStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreJobStore.class);

    private static final long ESTIMATED_TIME_TO_RELEASE_AND_ACQUIRE_TRIGGER = 10L;

    private final DatastoreCollection<JobDetailWrapper> jobDetailDatastoreCollection;

    private final DatastoreCollection<OperableTriggerWrapper> triggerDatastoreCollection;

    private final DatastoreCollection<CalendarWrapper> calendarDatastoreCollection;

    @SuppressWarnings("unused")
    private SchedulerSignaler signaler;

    @Inject
    public DatastoreJobStore(final DatastoreCollectionRegistry collectionRegistry) {
        this.jobDetailDatastoreCollection = collectionRegistry.get(JobDetailWrapper.class);
        this.triggerDatastoreCollection = collectionRegistry.get(OperableTriggerWrapper.class);
        this.calendarDatastoreCollection = collectionRegistry.get(CalendarWrapper.class);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#initialize(org.quartz.spi.ClassLoadHelper, org.quartz.spi.SchedulerSignaler)
     */
    @Override
    public void initialize(final ClassLoadHelper loadHelper, final SchedulerSignaler signaler)
            throws SchedulerConfigException {
        LOGGER.info("initialize()");
        this.signaler = signaler;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#schedulerStarted()
     */
    @Override
    public void schedulerStarted() throws SchedulerException {
        LOGGER.info("schedulerStarted()");
        recoverJobsAndTriggers();
    }

    protected void recoverJobsAndTriggers() {
        final Iterable<OperableTriggerWrapper> triggers = triggerDatastoreCollection.findAll();
        for (final Iterator<OperableTriggerWrapper> iterator = triggers.iterator(); iterator.hasNext();) {
            final OperableTriggerWrapper trigger = iterator.next();
            switch (trigger.getOperableTriggerState()) {
                case ACQUIRED:
                case BLOCKED:
                    trigger.setOperableTriggerState(OperableTriggerState.WAITING);
                    break;
                case PAUSED_BLOCKED:
                    trigger.setOperableTriggerState(OperableTriggerState.PAUSED);
                    break;
                case EXECUTING:
                case COMPLETE:
                case ERROR:
                default:
                    break;
            }

            triggerDatastoreCollection.update(trigger);
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#schedulerPaused()
     */
    @Override
    public void schedulerPaused() {
        LOGGER.info("schedulerPaused()");
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#schedulerResumed()
     */
    @Override
    public void schedulerResumed() {
        LOGGER.info("schedulerResumed()");
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#shutdown()
     */
    @Override
    public void shutdown() {
        LOGGER.info("shutdown()");
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#supportsPersistence()
     */
    @Override
    public boolean supportsPersistence() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getEstimatedTimeToReleaseAndAcquireTrigger()
     */
    @Override
    public long getEstimatedTimeToReleaseAndAcquireTrigger() {
        final long estimatedTimeToReleaseAndAcquireTrigger = ESTIMATED_TIME_TO_RELEASE_AND_ACQUIRE_TRIGGER;
        return estimatedTimeToReleaseAndAcquireTrigger;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#isClustered()
     */
    @Override
    public boolean isClustered() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#storeJobAndTrigger(org.quartz.JobDetail, org.quartz.spi.OperableTrigger)
     */
    @Override
    public void storeJobAndTrigger(final JobDetail newJob, final OperableTrigger newTrigger)
            throws ObjectAlreadyExistsException, JobPersistenceException {
        LOGGER.info("storeJobAndTrigger()");
        storeJob(newJob, false);
        storeTrigger(newTrigger, false);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#storeJob(org.quartz.JobDetail, boolean)
     */
    @Override
    public void storeJob(final JobDetail newJob, final boolean replaceExisting) throws ObjectAlreadyExistsException,
            JobPersistenceException {
        LOGGER.info("storeJob()");
        final JobDetailWrapper wrapper = new JobDetailWrapper();
        wrapper.setJobKey(newJob.getKey());
        wrapper.setJobDetail(newJob);

        jobDetailDatastoreCollection.save(wrapper);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#storeJobsAndTriggers(java.util.Map, boolean)
     */
    @Override
    public void storeJobsAndTriggers(final Map<JobDetail, Set<? extends Trigger>> triggersAndJobs, final boolean replace)
            throws ObjectAlreadyExistsException, JobPersistenceException {
        LOGGER.info("storeJobsAndTriggers()");
        for (final Entry<JobDetail, Set<? extends Trigger>> entry : triggersAndJobs.entrySet()) {
            storeJob(entry.getKey(), replace);
            for (final Trigger trigger : entry.getValue()) {
                storeTrigger((OperableTrigger) trigger, replace);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#removeJob(org.quartz.JobKey)
     */
    @Override
    public boolean removeJob(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("removeJob()");
        return jobDetailDatastoreCollection.delete(findJobDetailByKey(jobKey));
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#removeJobs(java.util.List)
     */
    @Override
    public boolean removeJobs(final List<JobKey> jobKeys) throws JobPersistenceException {
        LOGGER.info("removeJobs()");
        boolean value = true;
        for (final JobKey jobKey : jobKeys) {
            value = value && removeJob(jobKey);
        }
        return value;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#retrieveJob(org.quartz.JobKey)
     */
    @Override
    public JobDetail retrieveJob(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("retrieveJob()");
        return findJobDetailByKey(jobKey).getJobDetail();
    }

    private JobDetailWrapper findJobDetailByKey(final JobKey jobKey) {
        final Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("group", jobKey.getGroup());
        fieldMap.put("name", jobKey.getName());

        return jobDetailDatastoreCollection.find(fieldMap);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#storeTrigger(org.quartz.spi.OperableTrigger, boolean)
     */
    @Override
    public void storeTrigger(final OperableTrigger newTrigger, final boolean replaceExisting)
            throws ObjectAlreadyExistsException, JobPersistenceException {
        LOGGER.info("storeTrigger()");
        final OperableTriggerWrapper triggerWrapper = new OperableTriggerWrapper();
        triggerWrapper.setTriggerKey(newTrigger.getKey());
        triggerWrapper.setJobKey(newTrigger.getJobKey());
        triggerWrapper.setOperableTriggerState(OperableTriggerState.WAITING);
        triggerWrapper.setOperableTrigger(newTrigger);

        this.triggerDatastoreCollection.save(triggerWrapper);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#removeTrigger(org.quartz.TriggerKey)
     */
    @Override
    public boolean removeTrigger(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("removeTrigger()");
        return triggerDatastoreCollection.delete(findTriggerByKey(triggerKey));
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#removeTriggers(java.util.List)
     */
    @Override
    public boolean removeTriggers(final List<TriggerKey> triggerKeys) throws JobPersistenceException {
        LOGGER.info("removeTriggers()");
        boolean value = true;
        for (final TriggerKey triggerKey : triggerKeys) {
            value = value && removeTrigger(triggerKey);
        }

        return value;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#replaceTrigger(org.quartz.TriggerKey, org.quartz.spi.OperableTrigger)
     */
    @Override
    public boolean replaceTrigger(final TriggerKey triggerKey, final OperableTrigger newTrigger)
            throws JobPersistenceException {
        LOGGER.info("replaceTrigger()");
        removeTrigger(triggerKey);
        storeTrigger(newTrigger, false);
        return false;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#retrieveTrigger(org.quartz.TriggerKey)
     */
    @Override
    public OperableTrigger retrieveTrigger(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("retrieveTrigger");
        return findTriggerByKey(triggerKey).getOperableTrigger();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#checkExists(org.quartz.JobKey)
     */
    @Override
    public boolean checkExists(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("checkExists (jobKey)");
        return retrieveJob(jobKey) != null;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#checkExists(org.quartz.TriggerKey)
     */
    @Override
    public boolean checkExists(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("checkExists (trigger)");
        return retrieveTrigger(triggerKey) != null;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#clearAllSchedulingData()
     */
    @Override
    public void clearAllSchedulingData() throws JobPersistenceException {
        LOGGER.info("clearAllSchedulingData()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#storeCalendar(java.lang.String, org.quartz.Calendar, boolean, boolean)
     */
    @Override
    public void storeCalendar(final String name, final Calendar calendar, final boolean replaceExisting,
            final boolean updateTriggers) throws ObjectAlreadyExistsException, JobPersistenceException {
        LOGGER.info("storeCalendar()");
        final CalendarWrapper calendarWrapper = new CalendarWrapper();
        calendarWrapper.setCalendarName(name);
        calendarWrapper.setCalendar(calendar);
        calendarDatastoreCollection.save(calendarWrapper);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#removeCalendar(java.lang.String)
     */
    @Override
    public boolean removeCalendar(final String calName) throws JobPersistenceException {
        LOGGER.info("removeCalendar()");
        return calendarDatastoreCollection.delete(findCalendarByKey(calName));
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#retrieveCalendar(java.lang.String)
     */
    @Override
    public Calendar retrieveCalendar(final String calName) throws JobPersistenceException {
        LOGGER.info("retrieveCalendar()");

        return findCalendarByKey(calName).getCalendar();
    }

    private CalendarWrapper findCalendarByKey(final String key) {
        final Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("calendarName", key);

        return calendarDatastoreCollection.find(fieldMap);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getNumberOfJobs()
     */
    @Override
    public int getNumberOfJobs() throws JobPersistenceException {
        LOGGER.info("getNumberOfJobs()");
        return iterableCounter(jobDetailDatastoreCollection.findAll());
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getNumberOfTriggers()
     */
    @Override
    public int getNumberOfTriggers() throws JobPersistenceException {
        LOGGER.info("getNumberOfTriggers()");
        return iterableCounter(triggerDatastoreCollection.findAll());
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getNumberOfCalendars()
     */
    @Override
    public int getNumberOfCalendars() throws JobPersistenceException {
        LOGGER.info("getNumberOfCalendars()");
        return iterableCounter(calendarDatastoreCollection.findAll());
    }

    public <T> int iterableCounter(final Iterable<T> iterable) {
        int count = 0;
        for (final Iterator<T> iterator = iterable.iterator(); iterator.hasNext(); ++count) {
            iterator.next();
        }
        return count;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getJobKeys(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Set<JobKey> getJobKeys(final GroupMatcher<JobKey> matcher) throws JobPersistenceException {
        LOGGER.info("getJobKeys()");
        final Iterable<JobDetailWrapper> jobDetailWrappers = jobDetailDatastoreCollection.findAll();
        final Set<JobKey> jobKeys = new TreeSet<JobKey>();
        for (final Iterator<JobDetailWrapper> iterator = jobDetailWrappers.iterator(); iterator.hasNext();) {
            final JobKey jobKey = iterator.next().getJobKey();
            if (!jobKeys.contains(jobKey)) {
                jobKeys.add(jobKey);
            }
        }

        return jobKeys;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getTriggerKeys(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Set<TriggerKey> getTriggerKeys(final GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        LOGGER.info("getTriggerKeys()");
        final Iterable<OperableTriggerWrapper> triggerWrappers = triggerDatastoreCollection.findAll();
        final Set<TriggerKey> triggerKeys = new TreeSet<TriggerKey>();

        for (final Iterator<OperableTriggerWrapper> iterator = triggerWrappers.iterator(); iterator.hasNext();) {
            final TriggerKey triggerKey = iterator.next().getTriggerKey();
            if (!triggerKeys.contains(triggerKey)) {
                triggerKeys.add(triggerKey);
            }
        }

        return triggerKeys;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getJobGroupNames()
     */
    @Override
    public List<String> getJobGroupNames() throws JobPersistenceException {
        LOGGER.info("getJobGroupNames()");

        final Iterable<JobDetailWrapper> jobDetailWrappers = jobDetailDatastoreCollection.findAll();
        final List<String> jobGroupNames = new ArrayList<String>();
        for (final Iterator<JobDetailWrapper> iterator = jobDetailWrappers.iterator(); iterator.hasNext();) {
            final JobKey jobKey = iterator.next().getJobKey();
            if (!jobGroupNames.contains(jobKey.getGroup())) {
                jobGroupNames.add(jobKey.getGroup());
            }
        }

        return jobGroupNames;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getTriggerGroupNames()
     */
    @Override
    public List<String> getTriggerGroupNames() throws JobPersistenceException {
        LOGGER.info("getTriggerGroupNames()");

        final Iterable<OperableTriggerWrapper> triggerWrappers = triggerDatastoreCollection.findAll();
        final List<String> triggerGroupNames = new ArrayList<String>();

        for (final Iterator<OperableTriggerWrapper> iterator = triggerWrappers.iterator(); iterator.hasNext();) {
            final TriggerKey triggerKey = iterator.next().getTriggerKey();
            if (!triggerGroupNames.contains(triggerKey.getGroup())) {
                triggerGroupNames.add(triggerKey.getGroup());
            }
        }

        return triggerGroupNames;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getCalendarNames()
     */
    @Override
    public List<String> getCalendarNames() throws JobPersistenceException {
        LOGGER.info("getCalendarNames()");
        final List<String> calendarNames = new ArrayList<String>();
        final Iterable<CalendarWrapper> calendars = calendarDatastoreCollection.findAll();

        for (final Iterator<CalendarWrapper> iterator = calendars.iterator(); iterator.hasNext();) {
            final CalendarWrapper wrapper = iterator.next();
            if (!calendarNames.contains(wrapper.getCalendarName())) {
                calendarNames.add(wrapper.getCalendarName());
            }
        }

        return calendarNames;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getTriggersForJob(org.quartz.JobKey)
     */
    @Override
    public List<OperableTrigger> getTriggersForJob(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("getTriggersForJob()");
        final List<OperableTrigger> triggers = new ArrayList<OperableTrigger>();
        final Iterable<OperableTriggerWrapper> operableTriggers = triggerDatastoreCollection.findAll();

        for (final Iterator<OperableTriggerWrapper> iterator = operableTriggers.iterator(); iterator.hasNext();) {
            final OperableTriggerWrapper wrapper = iterator.next();
            if (!triggers.contains(wrapper.getOperableTrigger()) && wrapper.getJobKey().equals(jobKey)) {
                triggers.add(wrapper.getOperableTrigger());
            }
        }

        return triggers;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getTriggerState(org.quartz.TriggerKey)
     */
    @Override
    public TriggerState getTriggerState(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("getTriggerState()");
        final OperableTriggerWrapper trigger = findTriggerByKey(triggerKey);
        switch (trigger.getOperableTriggerState()) {
            case BLOCKED:
            case PAUSED_BLOCKED:
                return TriggerState.BLOCKED;
            case COMPLETE:
                return TriggerState.COMPLETE;
            case ERROR:
                return TriggerState.ERROR;
            case ACQUIRED:
            case EXECUTING:
            case WAITING:
                return TriggerState.NORMAL;
            case PAUSED:
                return TriggerState.PAUSED;
            default:
                return TriggerState.NONE;
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#pauseTrigger(org.quartz.TriggerKey)
     */
    @Override
    public void pauseTrigger(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("pauseTrigger()");
        setTriggerState(findTriggerByKey(triggerKey), OperableTriggerState.PAUSED);
    }

    private OperableTriggerWrapper findTriggerByKey(final TriggerKey triggerKey) throws JobPersistenceException {
        final Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("triggerGroup", triggerKey.getGroup());
        fieldMap.put("triggerName", triggerKey.getName());

        return triggerDatastoreCollection.find(fieldMap);
    }

    private void setTriggerState(final OperableTriggerWrapper trigger, final OperableTriggerState state) {
        trigger.setOperableTriggerState(state);
        triggerDatastoreCollection.update(trigger);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#pauseTriggers(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Collection<String> pauseTriggers(final GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        LOGGER.info("pauseTriggers()");
        final Iterable<OperableTriggerWrapper> triggerWrappers = triggerDatastoreCollection.findAll();
        final Set<String> pausedGroups = new TreeSet<String>();

        for (final Iterator<OperableTriggerWrapper> iterator = triggerWrappers.iterator(); iterator.hasNext();) {
            final TriggerKey triggerKey = iterator.next().getTriggerKey();
            if (matcher.isMatch(triggerKey)) {
                if (!pausedGroups.contains(triggerKey.getGroup())) {
                    pausedGroups.add(triggerKey.getGroup());
                }
                pauseTrigger(triggerKey);
            }
        }

        return pausedGroups;
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#pauseJob(org.quartz.JobKey)
     */
    @Override
    public void pauseJob(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("pauseJob()");
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#pauseJobs(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Collection<String> pauseJobs(final GroupMatcher<JobKey> groupMatcher) throws JobPersistenceException {
        LOGGER.info("pauseJobs()");
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#resumeTrigger(org.quartz.TriggerKey)
     */
    @Override
    public void resumeTrigger(final TriggerKey triggerKey) throws JobPersistenceException {
        LOGGER.info("resumeTrigger()");
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#resumeTriggers(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Collection<String> resumeTriggers(final GroupMatcher<TriggerKey> matcher) throws JobPersistenceException {
        LOGGER.info("resumeTriggers()");
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#getPausedTriggerGroups()
     */
    @Override
    public Set<String> getPausedTriggerGroups() throws JobPersistenceException {
        LOGGER.info("getPausedTriggerGroups()");
        // TODO Auto-generated method stub
        return new HashSet<>();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#resumeJob(org.quartz.JobKey)
     */
    @Override
    public void resumeJob(final JobKey jobKey) throws JobPersistenceException {
        LOGGER.info("resumeJob()");
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#resumeJobs(org.quartz.impl.matchers.GroupMatcher)
     */
    @Override
    public Collection<String> resumeJobs(final GroupMatcher<JobKey> matcher) throws JobPersistenceException {
        LOGGER.info("resumeJobs()");
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#pauseAll()
     */
    @Override
    public void pauseAll() throws JobPersistenceException {
        LOGGER.info("pauseAll()");
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#resumeAll()
     */
    @Override
    public void resumeAll() throws JobPersistenceException {
        LOGGER.info("resumeAll()");
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#acquireNextTriggers(long, int, long)
     */
    @Override
    public List<OperableTrigger> acquireNextTriggers(final long noLaterThan, final int maxCount, final long timeWindow)
            throws JobPersistenceException {
        LOGGER.info("acquireNextTriggers(noLaterThan {}, maxcount {}, timeWindow {})", noLaterThan, maxCount,
                timeWindow);
        final List<OperableTrigger> acquiredTriggers = new ArrayList<OperableTrigger>();

        int numAcquired = 0;
        final Iterable<OperableTriggerWrapper> triggerWrappers = triggerDatastoreCollection.findAll();
        for (final Iterator<OperableTriggerWrapper> iterator = triggerWrappers.iterator(); iterator.hasNext();) {
            final OperableTriggerWrapper wrapper = iterator.next();
            if (isWaiting(wrapper) && getNextFireTime(wrapper) < noLaterThan + timeWindow) {
                setTriggerState(wrapper, OperableTriggerState.ACQUIRED);
                acquiredTriggers.add(wrapper.getOperableTrigger());
                if (++numAcquired >= maxCount) {
                    break;
                }
            }
        }

        return acquiredTriggers;
    }

    private long getNextFireTime(final OperableTriggerWrapper trigger) {
        return trigger.getOperableTrigger().getNextFireTime().getTime();
    }

    private boolean isWaiting(final OperableTriggerWrapper trigger) {
        return trigger.getOperableTriggerState().equals(OperableTriggerState.WAITING);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#releaseAcquiredTrigger(org.quartz.spi.OperableTrigger)
     */
    @Override
    public void releaseAcquiredTrigger(final OperableTrigger trigger) {
        LOGGER.info("releaseAcquiredTrigger() {}, {}", trigger, trigger.getKey().toString());
        try {
            if (trigger.getNextFireTime() != null) {
                setTriggerState(findTriggerByKey(trigger.getKey()), OperableTriggerState.WAITING);
            } else {
                setTriggerState(findTriggerByKey(trigger.getKey()), OperableTriggerState.COMPLETE);
            }
        } catch (final JobPersistenceException e) {
            LOGGER.error("Unable to release acquired trigger", e);
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#triggersFired(java.util.List)
     */
    @Override
    public List<TriggerFiredResult> triggersFired(final List<OperableTrigger> triggers) throws JobPersistenceException {
        final List<TriggerFiredResult> results = new ArrayList<TriggerFiredResult>();
        for (final OperableTrigger trigger : triggers) {
            final OperableTriggerWrapper wrapper = findTriggerByKey(trigger.getKey());
            Calendar calendar = null;
            if (trigger.getCalendarName() != null) {
                calendar = findCalendarByKey(trigger.getCalendarName()).getCalendar();
            }
            final JobDetailWrapper job = findJobDetailByKey(trigger.getJobKey());
            final Date previousFireTime = trigger.getPreviousFireTime();
            final Date scheduledFireTime = trigger.getNextFireTime();
            final Date nextFireTime = calculateNextFireTime(trigger, calendar);
            if (nextFireTime == null) {
                wrapper.setOperableTriggerState(OperableTriggerState.COMPLETE);
            } else {
                wrapper.setOperableTriggerState(OperableTriggerState.WAITING);
            }
            wrapper.setOperableTrigger(trigger);
            triggerDatastoreCollection.update(wrapper);

            final Date now = new Date();
            final TriggerFiredResult result =
                    new TriggerFiredResult(new TriggerFiredBundle(job.getJobDetail(), trigger, calendar, false, now,
                            scheduledFireTime, previousFireTime, nextFireTime));
            LOGGER.info("triggersFired: {}, {}", trigger.getKey(), trigger.getNextFireTime());

            results.add(result);
        }

        return results;
    }

    private Date calculateNextFireTime(final OperableTrigger trigger, final Calendar calendar) {
        final int misfireInstruction = trigger.getMisfireInstruction();
        LOGGER.info("misfire instruction: " + misfireInstruction);
        final Date now = new Date();

        // TODO Only do this with the appropriate misfire instruction
        while (trigger.getNextFireTime() != null && trigger.getNextFireTime().getTime() < now.getTime()) {
            trigger.triggered(calendar);
        }

        return trigger.getNextFireTime();
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#triggeredJobComplete(org.quartz.spi.OperableTrigger,
     *      org.quartz.JobDetail, org.quartz.Trigger.CompletedExecutionInstruction)
     */
    @Override
    public void triggeredJobComplete(final OperableTrigger trigger, final JobDetail jobDetail,
            final CompletedExecutionInstruction triggerInstCode) {
        LOGGER.info("triggeredJobComplete() :: " + triggerInstCode);
        if (jobDetail.isPersistJobDataAfterExecution()) {
            final JobDetailWrapper wrapper = findJobDetailByKey(jobDetail.getKey());
            wrapper.setJobDetail(jobDetail);
            jobDetailDatastoreCollection.update(wrapper);
        }
        if (CompletedExecutionInstruction.DELETE_TRIGGER.equals(triggerInstCode)) {
            try {
                jobDetailDatastoreCollection.delete(findJobDetailByKey(jobDetail.getKey()));
                triggerDatastoreCollection.delete(findTriggerByKey(trigger.getKey()));
            } catch (final JobPersistenceException e) {
                LOGGER.warn("Problem removing trigger", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#setInstanceId(java.lang.String)
     */
    @Override
    public void setInstanceId(final String schedInstId) {
        LOGGER.info("setInstanceId({})", schedInstId);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#setInstanceName(java.lang.String)
     */
    @Override
    public void setInstanceName(final String schedName) {
        LOGGER.info("setInstanceName({})", schedName);
    }

    /* (non-Javadoc)
     * @see org.quartz.spi.JobStore#setThreadPoolSize(int)
     */
    @Override
    public void setThreadPoolSize(final int poolSize) {
        LOGGER.info("setThreadPoolSize({})", poolSize);
    }
}
