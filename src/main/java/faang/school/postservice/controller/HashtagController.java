package faang.school.postservice.controller;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping("/{count}")
    public List<HashtagDto> getTopXPopularHashtags(@PathVariable int count) {
        return hashtagService.findTopXPopularHashtags(count);
    }
}
