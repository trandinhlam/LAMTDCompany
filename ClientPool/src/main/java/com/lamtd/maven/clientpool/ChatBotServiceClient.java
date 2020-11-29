package com.lamtd.maven.clientpool;

import com.lamtd.maven.lamtddal.FeedNewsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author lamtd
 */
@EnableConfigurationProperties
public class ChatBotServiceClient {

    public static ChatBotServiceClient INST;

    static {
    }

    @Autowired
    ChatBotEndpoint endpoint;

    public void sendText(String text) {
        System.err.println(endpoint);
    }

    public void sendNews(FeedNewsEntity t) {

    }
}
