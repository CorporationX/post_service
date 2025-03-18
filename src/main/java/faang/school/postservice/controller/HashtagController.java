package faang.school.postservice.controller;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HashtagController {

    @GetMapping
    public Page<PostResponseDto> getPostsByHashtag(HashtagRequestDto hashtagRequestDto) {
        return null;
    }
}
