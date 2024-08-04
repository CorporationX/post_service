package faang.school.postservice.config.postmoderator;

import faang.school.postservice.job.moderation.OtherAPostModerator;
import faang.school.postservice.job.moderation.OtherBPostModerator;
import faang.school.postservice.job.moderation.SwearingPostModerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostModeratorConfig {

    @Autowired
    private OtherAPostModerator otherAPostModerator;

    @Autowired
    private SwearingPostModerator swearingPostModerator;

    @Autowired
    private OtherBPostModerator otherBPostModerator;


    @PostConstruct
    public void setUp() {
        swearingPostModerator.setNext(otherAPostModerator);
        otherAPostModerator.setNext(otherBPostModerator);
    }

    @Bean
    public SwearingPostModerator postModerator() {
        return swearingPostModerator;
    }
}