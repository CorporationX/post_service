package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.hash.FeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/feed")
public class FeedController {
    private final FeedHeater feedHeater;

    @PostMapping("/heat")
    public void create() {
        feedHeater.feedHeat();
    }
}
