package faang.school.postservice.facade;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
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
}
