package io.ballerina.stdlib.task.server;

import io.ballerina.runtime.api.Environment;
import io.ballerina.stdlib.task.exceptions.SchedulingException;
import io.ballerina.stdlib.task.objects.TaskManager;
import io.ballerina.stdlib.task.utils.TaskConstants;
import io.ballerina.stdlib.task.utils.Utils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.Properties;

public class TaskServerImpl implements TaskServer {
    private final String serverName;
    public static final TaskManager taskManager = TaskManager.getInstance();

    public TaskServerImpl(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void start(Environment env, Properties properties) throws Exception {
        getScheduler(env);
        taskManager.getScheduler(properties, env).start();
    }

    @Override
    public void stop() throws Exception {
        // Implementation for stopping the task server
    }

    @Override
    public void pause() throws Exception {
        // Implementation for pausing the task server
    }

    @Override
    public void resume() throws Exception {
        // Implementation for resuming the task server
    }

    private static Scheduler getScheduler(Environment env) throws SchedulingException, SchedulerException {
        Utils.disableQuartzLogs();
        return TaskManager.getInstance().getScheduler(Utils.createSchedulerProperties(
                TaskConstants.QUARTZ_THREAD_COUNT_VALUE, TaskConstants.QUARTZ_THRESHOLD_VALUE), env);
    }
}
