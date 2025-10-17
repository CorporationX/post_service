package faang.school.postservice.controller.facade.post;

import faang.school.postservice.dto.post.CreateDraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public PostDto updatePost(Long postId, UpdatePostDto updatePostDto) {
        Post result = postService.updatePost(postId, updatePostDto);
        return postMapping.toPostDto(result);
    }

    public void deleteById(Long postId) {
        postService.deleteById(postId);
    }

    public PostDto getById(Long postId) {
        Post result = postService.getById(postId);
        return postMapping.toPostDto(result);
    }

    public List<PostDto> getDraftPostByAuthorId(Long authorId) {
        List<Post> result = postService.getDraftPostByAuthorId(authorId);
        log.info("A list of draft posts by author ID {} was found and sorted from new to old.", authorId);
        return mapToPostDtos(result);
    }

    public List<PostDto> getDraftPostByProjectId(Long projectId) {
        List<Post> result = postService.getDraftPostByProjectId(projectId);
        log.info("A list of draft posts by project ID {} was found and sorted from new to old.", projectId);
        return mapToPostDtos(result);
    }

    public List<PostDto> getPublishedPostByAuthorId(Long authorId) {
        List<Post> result = postService.getPublishedPostByAuthorId(authorId);
        log.info("A list of published posts by author ID {} was found and sorted from new to old.", authorId);
        return mapToPostDtos(result);
    }

    public List<PostDto> getPublishedPostByProjectId(Long projectId) {
        List<Post> result = postService.getPublishedPostByProjectId(projectId);
        log.info("A list of published posts by project ID {} was found and sorted from new to old.", projectId);
        return mapToPostDtos(result);
    }

    private List<PostDto> mapToPostDtos(List<Post> result) {
        return result.stream()
                .map(postMapping::toPostDto)
                .toList();
    }
}
