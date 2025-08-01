package faang.school.postservice.controller;

import faang.school.postservice.annotation.CachePublishedPost;
import faang.school.postservice.annotation.CacheUpdatePost;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostControllerFacade {

    private final PostService postService;

    public ResponsePostDto createPost(PostCreateDto postCreateDto) {
        Post post = PostMapper.postUpdateDtoToPost(postCreateDto);
        Post savedPost = postService.createPost(post);
        return PostMapper.postToResponsePostDto(savedPost);
    }

    @CachePublishedPost
    public ResponsePostDto publishPost(Long postId) {
        Post publishedPost = postService.publishPost(postId);
        return PostMapper.postToResponsePostDto(publishedPost);
    }

    @CacheUpdatePost
    public ResponsePostDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Post updatedFields = PostMapper.postUpdateDtoToPost(postUpdateDto);
        Post updatedPost = postService.updatePost(postId, updatedFields);
        return PostMapper.postToResponsePostDto(updatedPost);
    }

    public ResponsePostDto getPostById(Long postId) {
        Post post = postService.getPostById(postId);
        return PostMapper.postToResponsePostDto(post);
    }

    public List<ResponsePostDto> getUserDrafts(Long authorId) {
        List<Post> postList = postService.getAllDraftsByAuthorId(authorId);
        return PostMapper.postListToResponsePostDtoList(postList);
    }

    public List<ResponsePostDto> getProjectDrafts(Long projectId) {
        List<Post> postList = postService.getAllDraftsByProjectId(projectId);
        return PostMapper.postListToResponsePostDtoList(postList);
    }

    public List<ResponsePostDto> getUserPublished(Long authorId) {
        List<Post> postList = postService.getAllPublishedByAuthorId(authorId);
        return PostMapper.postListToResponsePostDtoList(postList);
    }

    public List<ResponsePostDto> getProjectPublished(Long projectId) {
        List<Post> postList = postService.getAllPublishedByProjectId(projectId);
        return PostMapper.postListToResponsePostDtoList(postList);
    }
}
