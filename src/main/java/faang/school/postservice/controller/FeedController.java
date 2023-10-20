package faang.school.postservice.controller;

import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
}
