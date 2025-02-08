package faang.school.postservice.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class FeedHeater {

    @Async(value = "feedHeaterPool")
    public void start() {

    }
}
