package faang.school.postservice.facade.post;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostFacade {
    private final PostService postService;
    private final PostMapper postMapper;

    public PostResponseDto createDraftPost(final PostCreateRequestDto postCreateRequestDto) {
        Post post = postMapper.toPostEntity(postCreateRequestDto);
        log.debug("Mapping PostCreateRequestDto to Post entity. DTO content: {}", postCreateRequestDto);

        post = postService.createDraftPost(post);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}", post);
        return postResponseDto;
    }

    public PostResponseDto publishPost(final long postId) {
        Post post = postService.publishPost(postId);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}", post);
        return postResponseDto;
    }
}
