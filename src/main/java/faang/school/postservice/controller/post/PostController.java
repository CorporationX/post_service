package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/post")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        postValidator.validateContentExists(postDto);

        return postService.createDraftPost(postDto);
    }

    public PostDto publishPost(long id) {
        return postService.publishPost(id);
    }
}