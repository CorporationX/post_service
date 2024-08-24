package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataDoesNotExistException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.exception.MessageError.DOES_NOT_EXIST_IN_DB;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostViewEventService postViewEventService;

    @Transactional(readOnly = true)
    public Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Post id=%d not found", id)));
    }

    public PostDto createDraftPost(PostDto dto) {
        Post post = postMapper.toEntity(dto);
        if (validateDraftPostPublisherExist(post)) {
            log.info("Draft Post was created successfully");
            postRepository.save(post);
        }
        return postMapper.toDto(post);
    }

    public PostDto publishPost(Long draftId) {
        Optional<Post> post = postRepository.findById(draftId);
        if (post.isPresent()) {
            if (post.get().isPublished()){
                log.info("The Post was already published at {}", post.get().getPublishedAt());
            } else {
                post.get().setPublished(true);
                post.get().setPublishedAt(LocalDateTime.now());
                log.info("Post with id = {} has been published successfully", draftId);
                postRepository.save(post.get());
            }
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", draftId);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setContent(postDto.getContent());
            post.get().setUpdatedAt(LocalDateTime.now());
            postRepository.save(post.get());
            log.info("Post with id = {} has been updated successfully", postId);
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", postId);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto deletePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setDeleted(true);
            postRepository.save(post.get());
            log.info("Post with id = {} was deleted successfully", postId);
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", postId);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postViewEventService.publishViewEvent(post.get());
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", postId);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public List<PostDto> getPostsSortedByDate(PostDto postDto) {
        List<Post> posts = new ArrayList<>();
        List<PostDto> sortedList;
        if (postDto.getAuthorId() != null) {
            posts = postRepository.findByAuthorId(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            posts = postRepository.findByProjectId(postDto.getProjectId());
        }
        if (!posts.isEmpty()) {
            if (postDto.isPublished()) {
                sortedList = posts.stream()
                        .filter(Post::isPublished)
                        .filter(post -> !post.isDeleted())
                        .sorted(Comparator.comparing(Post::getCreatedAt))
                        .map(postMapper::toDto)
                        .toList();
            } else {
                sortedList = posts.stream()
                        .filter(post -> !post.isPublished())
                        .filter(post -> !post.isDeleted())
                        .sorted(Comparator.comparing(Post::getCreatedAt))
                        .map(postMapper::toDto)
                        .toList();
            }
            sortedList.forEach(p -> postViewEventService.publishViewEvent(postMapper.toEntity(p)));
            return sortedList;
        } else {
            log.info("There's no one post in database written by your publisher");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    private boolean validateDraftPostPublisherExist(Post post) {
        boolean result = false;
        if (post.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(post.getAuthorId());
            if (userDto.getId() == null) {
                log.error("User id = {} doesn't exist in database", post.getAuthorId());
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;

        } else if (post.getProjectId() != null) {
            ProjectDto projectDto = projectServiceClient.getProject(post.getProjectId());
            if (projectDto.getId() == null) {
                log.error("Project with id = {} doesn't exist in database", post.getProjectId());
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;
        }
        return result;
    }
}
