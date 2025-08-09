package faang.school.postservice.controller.hashtag;

import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HashtagController — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 09.08.2025
 */
@RestController
@RequestMapping("/hashtags")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService service;

    @GetMapping("/top")
    public List<HashtagViewDto> getTop(@RequestParam(defaultValue = "0") Long offset,
                                       @RequestParam(defaultValue = "10") Long limit) {
        return service.getPopularHashtags(offset, limit);
    }

    @GetMapping("/{hashtag}")
    public List<PostViewDto> getList(@PathVariable String hashtag) {
        return service.getList(hashtag);
    }

}
