package faang.school.postservice.controller;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.service.hashtags.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hashtags")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping
    public Page<PostResponseDto> getPostsByHashtag(@RequestBody HashtagRequestDto hashtagRequestDto) {
        log.info("Starting request to get posts by hashtag: {}", hashtagRequestDto.getTag());
        Page<PostResponseDto> result = hashtagService.getPostsByHashtag(hashtagRequestDto);
        log.info("Finished request to get posts by hashtag: {}", hashtagRequestDto.getTag());
        return result;
    }
}
