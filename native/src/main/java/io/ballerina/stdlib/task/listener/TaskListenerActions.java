package io.ballerina.stdlib.task.listener;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.utils.TypeUtils;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.stdlib.task.TokenAcquisition;
import io.ballerina.stdlib.task.utils.Utils;

public class TaskListenerActions {
    public static final String NATIVE_LISTENER_KEY = "TASK_NATIVE_LISTENER";
    public static final BString TRIGGER_TIME = StringUtils.fromString("triggerTime");

    public static void initListener(BObject listener, BMap<BString, Object> listenerConfig) {
        TaskListener taskListener = new TaskListener();
        BMap<?, ?> schedule = listenerConfig.getMapValue(StringUtils.fromString("schedule"));
        BMap<?, ?> warmBackupConfig = listenerConfig.getMapValue(StringUtils.fromString("warmBackupConfig"));
        taskListener.setType(TypeUtils.getType(schedule).getName());
        if (TypeUtils.getType(schedule).getName().contains("OneTimeConfiguration")) {
            taskListener.setConfig("triggerTime", schedule.get(TRIGGER_TIME));
        } else {
            taskListener.setConfig("interval", schedule.get(StringUtils.fromString("interval")));
            taskListener.setConfig("maxCount", schedule.getIntValue(StringUtils.fromString("maxCount")));
            taskListener.setConfig("startTime", schedule.get(StringUtils.fromString("startTime")));
            taskListener.setConfig("endTime", schedule.get(StringUtils.fromString("endTime")));
            taskListener.setConfig("policy", schedule.get(StringUtils.fromString("taskPolicy")));
        }
        if (warmBackupConfig != null) {
            taskListener.setTaskCoordinator(true);
            taskListener.setConfig("warmBackupConfig", warmBackupConfig);
        }
        listener.addNativeData(NATIVE_LISTENER_KEY, taskListener);
    }

    public static Object startListener(Environment environment, BObject listenerObj) {
        TaskListener listener = (TaskListener) listenerObj.getNativeData(NATIVE_LISTENER_KEY);
        if (listener != null) {
            if (listener.getType().equals("OneTimeConfiguration")) {
                long triggerTime = (long) listener.getConfig().get("triggerTime");
                listener.start(environment, listenerObj, triggerTime);
            } else {
                if (listener.isTaskCoordinator()) {
                    BMap warmBackupConfig = (BMap) listener.getConfig().get("warmBackupConfig");
                    BMap<BString, Object> response;
                    try {
                        listener.start(environment, listenerObj,
                                (BDecimal) listener.getConfig().get("interval"),
                                (Long) listener.getConfig().get("maxCount"),
                                listener.getConfig().get("startTime"),
                                listener.getConfig().get("endTime"),
                                (BMap) listener.getConfig().get("policy"), warmBackupConfig);
                    } catch (Exception e) {

                    }
                } else {
                    listener.start(environment, listenerObj,
                            (BDecimal) listener.getConfig().get("interval"),
                            (Long) listener.getConfig().get("maxCount"),
                            listener.getConfig().get("startTime"),
                            listener.getConfig().get("endTime"),
                            (BMap) listener.getConfig().get("policy"));
                }
            }
        } else {
            return Utils.createTaskError("Listener not initialized");
        }
        return null;
    }

    public static Object attachService(BObject listenerObj, BObject service, BString serviceName) {
        TaskListener listener = (TaskListener) listenerObj.getNativeData(NATIVE_LISTENER_KEY);
        listener.registerService(serviceName.getValue(), service);
        // Schedule job if needed
        return null;
    }

    public static Object detachService(BObject listenerObj, BObject service, BString serviceName) {
        TaskListener listener = (TaskListener) listenerObj.getNativeData(NATIVE_LISTENER_KEY);
        listener.unregisterService(serviceName.getValue());
        // Unschedule job if needed
        return null;
    }

    // Similarly for start, stop, pause, resume, etc.
}

