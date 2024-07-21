package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public PostDto createDraft(@RequestBody PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("Invalid post! ProjectId or authorId is null!");
        }
        return postService.createDraft(postDto);
    }

    @GetMapping("/publish/{postId}")
    public PostDto publishDraft(@PathVariable Long postId) {
        if (postId == null) {
            throw new DataValidationException("Invalid postId!");
        }
        return postService.publishDraft(postId);
    }
}
