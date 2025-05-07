package io.ballerina.stdlib.task.listener;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.task.TokenAcquisition;
import io.ballerina.stdlib.task.exceptions.SchedulingException;
import io.ballerina.stdlib.task.objects.TaskManager;
import io.ballerina.stdlib.task.utils.TaskConstants;
import io.ballerina.stdlib.task.utils.Utils;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TaskListener {
    private static final int bound = 1000000;
    private static final String value = "1000";
    private Scheduler scheduler;
    private String type;
    private final Map<String, BObject> serviceRegistry = new ConcurrentHashMap<>();
    private Map<String, Object> configs = new ConcurrentHashMap<>();
    private Boolean isTaskCoordinator = false;

    public TaskListener() {
        // Initialize scheduler, etc.
    }

    public void start(Environment env, BObject job, long time) {
        Utils.disableQuartzLogs();
        try {
            getScheduler(env);
            for (String serviceName : serviceRegistry.keySet()) {
                Integer jobId = java.security.SecureRandom.getInstanceStrong().nextInt(bound);
                JobDataMap jobDataMap = getJobDataMap(job, TaskConstants.LOG_AND_CONTINUE, String.valueOf(jobId));
                BObject service = serviceRegistry.get(serviceName);
                TaskManager.getInstance().scheduleOneTimeListenerJob(jobDataMap, time, jobId, service);
            }
        } catch (SchedulerException | SchedulingException | IllegalArgumentException | NoSuchAlgorithmException e) {
        }
    }

    public void start(Environment env, BObject job, BDecimal interval, long maxCount,
                      Object startTime, Object endTime, BMap<BString, Object> policy) {
        try {
            getScheduler(env);
            for (String serviceName : serviceRegistry.keySet()) {
                int jobId = java.security.SecureRandom.getInstanceStrong().nextInt(bound);
                JobDataMap jobDataMap = getJobDataMap(job, ((BString) policy.get(TaskConstants.ERR_POLICY)).getValue(),
                        String.valueOf(jobId));
                BObject service = serviceRegistry.get(serviceName);
                TaskManager.getInstance().scheduleListenerIntervalJob(jobDataMap,
                        (interval.decimalValue().multiply(new BigDecimal(value))).longValue(), maxCount, startTime,
                        endTime, ((BString) policy.get(TaskConstants.WAITING_POLICY)).getValue(), jobId, service);
            }

        } catch (Exception e) {

        }
    }

    public void start(Environment env, BObject job, BDecimal interval, long maxCount,
                      Object startTime, Object endTime, BMap<BString, Object> policy, BMap warmBackupConfig) {
        try {
            getScheduler(env);
            for (String serviceName : serviceRegistry.keySet()) {
                int jobId = java.security.SecureRandom.getInstanceStrong().nextInt(bound);
                JobDataMap jobDataMap = getJobDataMap(job, ((BString) policy.get(TaskConstants.ERR_POLICY)).getValue(),
                        String.valueOf(jobId));
                BObject service = serviceRegistry.get(serviceName);
                BMap<Object, Object> databaseConfig = warmBackupConfig
                        .getMapValue(StringUtils.fromString("databaseConfig"));
                BString id = warmBackupConfig.getStringValue(StringUtils.fromString("taskId"));
                BString groupId = warmBackupConfig.getStringValue(StringUtils.fromString("groupId"));
                int livenessInterval = ((Long) warmBackupConfig
                        .get(StringUtils.fromString("livenessCheckInterval"))).intValue();
                int heartbeatFrequency = ((Long) warmBackupConfig
                        .get(StringUtils.fromString("heartbeatFrequency"))).intValue();
                BMap response = (BMap<BString, Object>) TokenAcquisition.acquireToken(
                        databaseConfig, id, groupId, false, livenessInterval, heartbeatFrequency);
                TaskManager.getInstance().scheduleListenerIntervalJobWithTokenCheck(jobDataMap,
                        (interval.decimalValue().multiply(new BigDecimal(value))).longValue(), maxCount, startTime,
                        endTime, ((BString) policy.get(TaskConstants.WAITING_POLICY)).getValue(),
                        jobId, response, service);
            }
        } catch (Exception e) {

        }
    }

    public void setConfig(String key, Object value) {
        configs.put(key, value);
    }

    public Map<String, Object> getConfig() {
        return configs;
    }

    public String getType() {
        return type;
    }

    public Boolean isTaskCoordinator() {
        return isTaskCoordinator;
    }

    public void setTaskCoordinator(Boolean isTaskCoordinator) {
        this.isTaskCoordinator = isTaskCoordinator;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void registerService(String serviceName, BObject service) {
        serviceRegistry.put(serviceName, service);
    }

    public void unregisterService(String serviceName) {
        serviceRegistry.remove(serviceName);
    }

    public BObject getService(String serviceName) {
        return serviceRegistry.get(serviceName);
    }

    private static Scheduler getScheduler(Environment env) throws SchedulingException, SchedulerException {
        Utils.disableQuartzLogs();
        return TaskManager.getInstance().getScheduler(Utils.createSchedulerProperties(
                TaskConstants.QUARTZ_THREAD_COUNT_VALUE, TaskConstants.QUARTZ_THRESHOLD_VALUE), env);
    }

    private static JobDataMap getJobDataMap(BObject job, String errorPolicy, String jobId) {
        Utils.disableQuartzLogs();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TaskConstants.JOB, job);
        jobDataMap.put(TaskConstants.ERROR_POLICY, errorPolicy);
        jobDataMap.put(TaskConstants.JOB_ID, jobId);
        return jobDataMap;
    }

    // Add methods for scheduling, pausing, resuming, etc.
}
