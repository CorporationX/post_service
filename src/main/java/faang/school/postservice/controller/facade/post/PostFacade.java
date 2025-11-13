package faang.school.postservice.controller.facade.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostFacade {

    private final PostMapper postMapping;
    private final PostService postService;
    private final UserContext userContext;
    private final PostViewEventPublisher eventPublisher;

    public PostDto createDraftPost(PostCreateDraftDto postCreateDraftDto) {
        Post post = postMapping.toPost(postCreateDraftDto);
        Post result = postService.createDraftPost(post);
        return postMapping.toPostDto(result);
    }

    public PostDto publishedPost(Long postId) {
        Post result = postService.publishedPost(postId);
        return postMapping.toPostDto(result);
    }

    public PostDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Post result = postService.updatePost(postId, postUpdateDto);
        return postMapping.toPostDto(result);
    }

    public void deleteById(Long postId) {
        postService.deleteById(postId);
    }

    public PostDto getById(Long postId) {
        Post result = postService.getById(postId);

        PostViewEvent event = new PostViewEvent(
                postId,
                result.getAuthorId(),
                userContext.getUserId(),
                LocalDateTime.now()
        );
        eventPublisher.publish(event);

        return postMapping.toPostDto(result);
    }

    public List<PostDto> getDraftPostByAuthorId(Long authorId) {
        List<Post> result = postService.getDraftPostByAuthorId(authorId);
        log.info("A list of draft posts by author ID {} was found and sorted from new to old.", authorId);
        return mapToPostDtos(result);
    }

    public List<PostDto> getPublishedPostByAuthorId(Long authorId) {
        List<Post> result = postService.getPublishedPostByAuthorId(authorId);
        log.info("A list of published posts by author ID {} was found and sorted from new to old.", authorId);
        return mapToPostDtos(result);
    }

    private List<PostDto> mapToPostDtos(List<Post> result) {
        return result.stream()
                .map(postMapping::toPostDto)
                .toList();
    }
}
