package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.RequestPostDto;
import faang.school.postservice.service.posts.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto create(@Valid @RequestBody RequestPostDto requestPostDto) {
        return postService.create(requestPostDto);
    }
}
