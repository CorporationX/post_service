package faang.school.postservice.controller;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagReadDto;
import faang.school.postservice.dto.hashtag.HashtagUpdateDto;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/hashtags")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagService hashtagService;

    @PostMapping
    public HashtagReadDto create(@RequestBody @Validated HashtagCreateDto createDto) {
        return hashtagService.create(createDto);
    }

    @PutMapping
    public HashtagReadDto update(@RequestBody @Validated HashtagUpdateDto updateDto) {
        return hashtagService.update(updateDto);
    }

    @GetMapping("/{hashtagId}")
    public HashtagReadDto getHashtag(@PathVariable long hashtagId) {
        return hashtagService.getHashtag(hashtagId);
    }

    @GetMapping
    public List<HashtagReadDto> getHashtagsByPostId(@RequestParam long postId) {
        return hashtagService.getHashtagsByPostId(postId);
    }

    @DeleteMapping("/{hashtagId}")
    public void remove(@PathVariable long hashtagId) {
        hashtagService.remove(hashtagId);
    }
}