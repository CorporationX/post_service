package faang.school.postservice.facade;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.user.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final UserCacheService userCacheService;
    private final UserServiceClient userServiceClient;

    public ResponsePostDto createPost(PostCreateDto postCreateDto) {
        Post post = PostMapper.postUpdateDtoToPost(postCreateDto);
        Post savedPost = postService.createPost(post);

        UserDto userDto = userServiceClient.getUser(post.getAuthorId());
        userCacheService.saveUserInCache(userDto);

        return PostMapper.postToResponsePostDto(savedPost);

    }

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
