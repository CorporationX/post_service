package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    public PostDto createDraftPost(PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        postValidator.validateContentExists(postDto);

        return postService.createDraftPost(postDto);
    }
}