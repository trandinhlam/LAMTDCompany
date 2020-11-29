package com.lamtd.maven.lamtddal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lamtd
 */
@Getter
@Setter
@ToString
public class FeedNewsEntity {

    private final int id;
    private final String title;
    private final String link;

    public FeedNewsEntity(int id, String title, String link) {
        this.id = id;
        this.title = title;
        this.link = link;
    }

}
