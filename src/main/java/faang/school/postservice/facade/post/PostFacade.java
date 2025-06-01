package faang.school.postservice.facade.post;

import faang.school.postservice.dto.post.PostCreateProjectRequestDto;
import faang.school.postservice.dto.post.PostCreateUserRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostFacade {
    private final PostService postService;
    private final PostMapper postMapper;

    public PostResponseDto createDraftPostForCurrentUser(PostCreateUserRequestDto postCreateUserRequestDto) {
        Post post = postMapper.toPostEntity(postCreateUserRequestDto);
        log.debug("Mapping PostCreateUserRequestDto to Post entity. DTO content: {}. Entity content: {}",
                postCreateUserRequestDto, post);

        post = postService.createDraftPostForCurrentUser(post);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}. DTO content: {}.",
                post, postResponseDto);
        return postResponseDto;
    }

    public PostResponseDto createDraftPostForProject(PostCreateProjectRequestDto postCreateProjectRequestDto) {
        Post post = postMapper.toPostEntity(postCreateProjectRequestDto);
        log.debug("Mapping PostCreateProjectRequestDto to Post entity. DTO content: {}. Entity content: {}",
                postCreateProjectRequestDto, post);

        post = postService.createDraftPostForProject(post);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}. DTO content: {}.",
                post, postResponseDto);
        return postResponseDto;
    }

    public PostResponseDto publishPost(long postId) {
        Post post = postService.publishPost(postId);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}. DTO content: {}.",
                post, postResponseDto);
        return postResponseDto;
    }

    public PostResponseDto updatePost(long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postService.getPostById(postId);

        postMapper.update(post, postUpdateRequestDto);
        log.debug("Mapping PostUpdateRequestDto to Post entity. DTO content: {}. Entity content: {}.",
                postUpdateRequestDto, post);

        post = postService.updatePost(post);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}. DTO content: {}.",
                post, postResponseDto);
        return postResponseDto;
    }

    public void deletePost(long postId) {
        postService.deletePost(postId);
    }

    public PostResponseDto getPostById(long postId) {
        Post post = postService.getPostById(postId);

        PostResponseDto postResponseDto = postMapper.toPostResponseDto(post);
        log.debug("Mapping Post entity to PostResponseDto. Entity content: {}. DTO content: {}.",
                post, postResponseDto);
        return postResponseDto;
    }

    public List<PostResponseDto> getAllDraftPostsByUserId(long userId) {
        List<Post> posts = postService.getAllDraftPostsByUserId(userId);

        List<PostResponseDto> postResponseDtoList = postMapper.toPostResponseDtoList(posts);
        log.debug("Mapping Post entity list to PostResponseDto list. Entity content: {}. DTO content: {}.",
                posts, postResponseDtoList);
        return postResponseDtoList;
    }

    public List<PostResponseDto> getAllDraftPostsByProjectId(long projectId) {
        List<Post> posts = postService.getAllDraftPostsByProjectId(projectId);

        List<PostResponseDto> postResponseDtoList = postMapper.toPostResponseDtoList(posts);
        log.debug("Mapping Post entity list to PostResponseDto list. Entity content: {}. DTO content: {}.",
                posts, postResponseDtoList);
        return postResponseDtoList;
    }

    public List<PostResponseDto> getAllPublishedPostsByUserId(long userId) {
        List<Post> posts = postService.getAllPublishedPostsByUserId(userId);

        List<PostResponseDto> postResponseDtoList = postMapper.toPostResponseDtoList(posts);
        log.debug("Mapping Post entity list to PostResponseDto list. Entity content: {}. DTO content: {}.",
                posts, postResponseDtoList);
        return postResponseDtoList;
    }

    public List<PostResponseDto> getAllPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postService.getAllPublishedPostsByProjectId(projectId);

        List<PostResponseDto> postResponseDtoList = postMapper.toPostResponseDtoList(posts);
        log.debug("Mapping Post entity list to PostResponseDto list. Entity content: {}. DTO content: {}.",
                posts, postResponseDtoList);
        return postResponseDtoList;
    }
}
