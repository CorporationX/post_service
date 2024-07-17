package faang.school.postservice.controller.hashtag;

import faang.school.postservice.annotation.ValidHashtag;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.hashtag.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hashtag")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping
    public List<PostDto> getPostsByHashtag(@ValidHashtag String hashtag) {
        return hashtagService.getPostsByHashtag(hashtag);
    }
}
