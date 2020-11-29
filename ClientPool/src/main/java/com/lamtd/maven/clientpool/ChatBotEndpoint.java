package com.lamtd.maven.clientpool;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lamtd
 */
@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "ChatBotEndpoint")
@EnableConfigurationProperties
public class ChatBotEndpoint {

    private String host;
    private int port;
}
