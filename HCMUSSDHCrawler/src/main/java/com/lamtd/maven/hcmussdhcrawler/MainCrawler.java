package com.lamtd.maven.hcmussdhcrawler;

import com.lamtd.maven.hcmussdhcrawler.task.HCMUSCrawlTask;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;

/**
 * @author lamtd
 */
@Log4j2
public class MainCrawler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new HCMUSCrawlTask(), 0, TimeUnit.MINUTES.toMillis(10));
    }
}
