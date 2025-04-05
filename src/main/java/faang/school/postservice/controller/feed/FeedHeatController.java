package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedHeatController {

    private final FeedHeaterService feedHeaterService;
    
    @PostMapping("/heat")
    public ResponseEntity<?> heatUserFeed() {
        feedHeaterService.heatFeed();
        return ResponseEntity.ok().build();
    }
}
