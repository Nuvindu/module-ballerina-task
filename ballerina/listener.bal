import ballerina/jballerina.java;
import ballerina/uuid;

# Listener for task scheduling.
public class Listener {
    private ListenerConfiguration config;
    
    # Initializes the task 'listener.
    # 
    # + config - The 'listener configuration
    public isolated function init(*ListenerConfiguration config) {
        self.config = config;
        initListener(self, config);
    }
    
    # Attaches a service to the 'listener.
    # 
    # + s - The service to be attached
    # + name - The service name
    # + return - An error if the service cannot be attached
    public isolated function attach(Service s, string[]|string? name = ()) returns error? {
        if name is string[] {
            foreach string serviceId in name {
                error? err = attachService(self, s, serviceId);
            }
        } else {
            string serviceId = name is string ? name : uuid:createRandomUuid();
            return attachService(self, s, serviceId);
        }
    }
    
    # Detaches a service from the 'listener.
    # 
    # + s - The service to be detached
    # + return - An error if the service cannot be detached
    public isolated function detach(Service s) returns error? {
        // return detachService(self, s);
    }
    
    # Starts the 'listener.
    # 
    # + return - An error if the 'listener fails to start
    public isolated function 'start() returns error? {
        return startListener(self);
    }

    # Stops the 'listener gracefully.
    # 
    # + return - An error if the 'listener fails to stop
    public isolated function gracefulStop() returns error? {
        // return gracefulStopListener(self);
    }
    
    # Stops the 'listener immediately.
    # 
    # + return - An error if the 'listener fails to stop
    public isolated function immediateStop() returns error? {
        // return immediateStopListener(self);
    }
    
    // # Schedules a job with the 'listener.
    // # 
    // # + s - The service to be scheduled
    // # + name - The service name
    // # + return - The job ID if successful, an error otherwise
    // public isolated function scheduleJob(Service s, string[]|string? name = ()) returns JobId|error {
    //     return scheduleJobInternal(self, s, serviceId);
    // }
    
    // # Unschedules a job from the 'listener.
    // # 
    // # + s - The service to be unscheduled
    // # + return - An error if the job cannot be unscheduled
    // public isolated function unscheduleJob(Service s) returns error? {
    //     return unscheduleJobInternal(self, s);
    // }
    
    // # Pauses all jobs managed by this 'listener.
    // # 
    // # + return - An error if the operation fails
    // public isolated function pauseAllJobs() returns error? {
    //     return pauseAllJobsInternal(self);
    // }
    
    // # Resumes all paused jobs managed by this 'listener.
    // # 
    // # + return - An error if the operation fails
    // public isolated function resumeAllJobs() returns error? {
    //     return resumeAllJobsInternal(self);
    // }
    
    // # Pauses a specific job.
    // # 
    // # + id - The ID of the job to pause
    // # + return - An error if the operation fails
    // public isolated function pauseJob(JobId id) returns error? {
    //     return pauseJobInternal(self, id);
    // }
    
    // # Resumes a specific paused job.
    // # 
    // # + id - The ID of the job to resume
    // # + return - An error if the operation fails
    // public isolated function resumeJob(JobId id) returns error? {
    //     return resumeJobInternal(self, id);
    // }
    
    // # Gets the list of running job IDs.
    // # 
    // # + return - An array of job IDs
    // public isolated function getRunningJobs() returns JobId[] {
    //     return getRunningJobsInternal(self);
    // }
}

isolated function initListener(Listener 'listener, ListenerConfiguration config) = @java:Method {
    'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
} external;

isolated function attachService(Listener 'listener, Service s, string serviceId) returns error? = @java:Method {
    'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
} external;

// isolated function detachService(Listener 'listener, Service s) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

isolated function startListener(Listener 'listener) returns error? = @java:Method {
    'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
} external;

// isolated function gracefulStopListener(Listener 'listener) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function immediateStopListener(Listener 'listener) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function scheduleJobInternal(Listener 'listener, Service s, string serviceId) returns JobId|error = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function unscheduleJobInternal(Listener 'listener, Service s) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function pauseAllJobsInternal(Listener 'listener) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function resumeAllJobsInternal(Listener 'listener) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function pauseJobInternal(Listener 'listener, JobId id) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function resumeJobInternal(Listener 'listener, JobId id) returns error? = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;

// isolated function getRunningJobsInternal(Listener 'listener) returns JobId[] = @java:Method {
//     'class: "io.ballerina.stdlib.task.listener.TaskListenerActions"
// } external;
