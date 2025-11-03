package faang.school.postservice.controller;

import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.service.HashTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hashTag")
public class HashTagController {
    private final HashTagService hashTagService;
    @GetMapping("/{tagName}")
    public List<PostDto> getPostsByTag(@PathVariable String tagName) {
        System.out.println("Запрос хэш тега");
        return hashTagService.getPostsByHashTag(tagName);
    }
}
