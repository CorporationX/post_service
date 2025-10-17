package faang.school.postservice.controller.post;

import faang.school.postservice.controller.facade.post.PostFacade;
import faang.school.postservice.dto.post.CreateDraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostFacade postFacade;

    @PostMapping
    public PostDto createDraftPost(CreateDraftPostDto createDraftPostDto) {
        return postFacade.createDraftPost(createDraftPostDto);
    }

    @PatchMapping("/publication/{postId}")
    public PostDto publishedPost(@PathVariable Long postId) {
        return postFacade.publishedPost(postId);
    }
}
