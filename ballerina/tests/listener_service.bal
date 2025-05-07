import ballerina/io;
import ballerina/time;

time:Utc currentUtc = time:utcNow();
time:Utc newTime = time:utcAddSeconds(currentUtc, 3);
time:Utc newTime2 = time:utcAddSeconds(currentUtc, 10);
time:Civil time = time:utcToCivil(newTime);

OneTimeConfiguration schedule = {
    triggerTime: check getTimeInMillies(time)
};

RecurringConfiguration recurSchedule = {
    interval: 2,
    maxCount: 5,
    startTime: time,
    taskPolicy: {}
};

listener Listener taskListener = new (schedule = schedule);

service "job-1" on taskListener {
    private int i = 1;

    remote function onTrigger() {
        lock {
            self.i += 1;
            io:println("MyCounter: ", self.i);
        }
    }
}

listener Listener taskListener2 = new (schedule = recurSchedule);

service "job-2" on taskListener2 {
    private int i = 1;

    remote function onTrigger() {
        lock {
            self.i += 1;
            io:println("MyCounter: ", self.i);
        }
    }
}
