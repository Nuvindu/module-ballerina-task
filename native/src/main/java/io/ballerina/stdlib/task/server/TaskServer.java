package io.ballerina.stdlib.task.server;

import io.ballerina.runtime.api.Environment;

import java.util.Properties;

public interface TaskServer {
    void start(Environment env, Properties properties) throws Exception;
    void stop() throws Exception;
    void pause() throws Exception;
    void resume() throws Exception;
}
