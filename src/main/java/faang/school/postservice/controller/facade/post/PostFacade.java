package faang.school.postservice.controller.facade.post;

import faang.school.postservice.dto.post.CreateDraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostFacade {

    private final PostMapper postMapping;
    private final PostService postService;

    public PostDto createDraftPost(CreateDraftPostDto createDraftPostDto) {
        Post post = postMapping.toPost(createDraftPostDto);
        Post result = postService.createDraftPost(post);
        return postMapping.toPostDto(result);
    }

    public PostDto publishedPost(Long postId) {
        Post result = postService.publishedPost(postId);
        return postMapping.toPostDto(result);
    }


}
