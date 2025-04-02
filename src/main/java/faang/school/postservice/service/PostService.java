package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.validationUtils.PostValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    public static final String CANT_UPDATE_DELETED_POST = "Can't update deleted post";
    public static final String NO_POST_FOUND = "No post found with ID %d";
    public static final String POST_HAS_ALREADY_BEEN_DELETED = "Post has already been deleted";

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public PostResponseDto createDraftPost(PostRequestDto postRequestDto) {
        PostValidation.validatePostAuthors(postRequestDto);
        PostValidation.validatePostDraftCreation(postRequestDto);
        Post post = postRepository.save(postMapper.toPost(postRequestDto));
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto publishPost(Long postId) {
        Optional<Post> postDraftOptional = postRepository.findById(postId);
        validatePostOptional(postDraftOptional, postId);
        Post post = postDraftOptional.get();
        PostValidation.validatePostInPublishing(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto updatePost(PostRequestDto postRequestDto) {
        PostValidation.validatePostUpdate(postRequestDto);
        Optional<Post> postOptional = postRepository.findById(postRequestDto.getId());
        validatePostOptional(postOptional, postRequestDto.getId());
        Post post = postOptional.get();

        if (post.isDeleted()) {
            log.error(CANT_UPDATE_DELETED_POST);
            throw new IllegalArgumentException(CANT_UPDATE_DELETED_POST);
        }
        int updateCounter = 0;
        if (!postRequestDto.getContent().equals(post.getContent())) {
            updateCounter++;
            post.setContent(postRequestDto.getContent());
        }
        if (updateCounter == 0) {
            log.warn("Nothing was updated for post with ID {}", postRequestDto.getId());
        }
        postRepository.save(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto deletePost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        validatePostOptional(postOptional, postId);

        Post post = postOptional.get();
        if (post.isDeleted()) {
            log.error(POST_HAS_ALREADY_BEEN_DELETED);
            throw new IllegalArgumentException(POST_HAS_ALREADY_BEEN_DELETED);
        }
        post.setDeleted(true);
        postRepository.save(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto getPostById(Long postId) {
        PostValidation.validatePostId(postId);
        Optional<Post> optionalPost = postRepository.findById(postId);
        validatePostOptional(optionalPost, postId);

        return postMapper.toPostResponseDto(optionalPost.get());
    }

    public List<PostResponseDto> getUserDraftPosts(Long userId) {
        PostValidation.validateUserId(userId);
        List<Post> drafts = postRepository.findDraftsByAuthorId(userId);
        return postMapper.toPostResponseDtoList(drafts);
    }

    public List<PostResponseDto> getProjectDraftPosts(Long projectId) {
        PostValidation.validateProjectId(projectId);
        List<Post> drafts = postRepository.findDraftsByProjectId(projectId);
        return postMapper.toPostResponseDtoList(drafts);
    }

    public List<PostResponseDto> getUserPublishedPosts(Long userId) {
        PostValidation.validateUserId(userId);
        List<Post> posts = postRepository.findPublishedByAuthorId(userId);
        return postMapper.toPostResponseDtoList(posts);
    }

    public List<PostResponseDto> getProjectPublishedPosts(Long projectId) {
        PostValidation.validateProjectId(projectId);
        List<Post> posts = postRepository.findPublishedByProjectId(projectId);
        return postMapper.toPostResponseDtoList(posts);
    }

    private void validatePostOptional(Optional<Post> postOptional, Long id) {
        if (postOptional.isEmpty()) {
            String message = String.format(NO_POST_FOUND, id);
            log.error(message);
            throw new PostNotFoundException(message);
        }
    }
}
