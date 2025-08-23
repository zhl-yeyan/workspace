package com.zhl.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@SpringBootTest
public class TimeTest {
    @Test
    public void testTime()throws InterruptedException{
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("任务1");
        Thread.sleep(1000);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }
}
