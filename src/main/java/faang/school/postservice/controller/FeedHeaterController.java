package faang.school.postservice.controller;

import faang.school.postservice.service.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/heat-feed")
@RequiredArgsConstructor
@Slf4j
public class FeedHeaterController {

    private final FeedHeaterService feedHeaterService;

    @PostMapping
    public void heatFeed() {
        log.info("Received request to heat feed");
        feedHeaterService.heatFeed();
    }
}
