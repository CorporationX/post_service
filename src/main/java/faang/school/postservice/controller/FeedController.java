package faang.school.postservice.controller;

import faang.school.postservice.dto.HeaterEvent;
import faang.school.postservice.heater.FeedHeater;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.producer.KafkaHeaterFeedProducer;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final KafkaHeaterFeedProducer kafkaHeaterFeedProducer;

    @GetMapping
    public List<CachePost> getFeed(@RequestParam(required = false) Long lastPostId) {
        return feedService.getFeed(lastPostId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        kafkaHeaterFeedProducer.send(new HeaterEvent(LocalDateTime.now()));
    }
}
