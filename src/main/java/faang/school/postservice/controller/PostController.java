package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private static final String ID_ERROR_MESSAGE = "ID must be positive";

    private final PostMapper postMapper;
    private final PostService postService;

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto getPost(@Positive(message = ID_ERROR_MESSAGE)
                                 @PathVariable Long postId) {

        return postMapper.toDto(
                postService.getPost(postId));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public PostDto createPost(@Validated @RequestBody PostDto postDto) {
        return postMapper.toDto(
                postService.createPost(postMapper.toEntity(postDto)));
    }
}
