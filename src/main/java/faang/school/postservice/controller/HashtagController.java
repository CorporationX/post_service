package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/hashtag")
public class HashtagController {
    private final HashtagService service;
    private final UserContext userContext;

    @PostMapping
    public HashtagDto addHashtagToPost(HashtagDto hashtagDto){
        return service.addHashtagToPost(hashtagDto, userContext.getUserId());
    }

    @GetMapping
    public List<PostDto> getPostsByHashtag(HashtagDto hashtagDto){
        return service.getPostsByHashtag(hashtagDto);
    }
}
