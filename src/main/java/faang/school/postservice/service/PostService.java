package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataDoesNotExistException;
import faang.school.postservice.logging.Logging;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.exception.MessageError.DOES_NOT_EXIST_IN_DB;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private Logging logging = new Logging();
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public PostDto createDraftPost(PostDto dto) {
        Post post = postMapper.toEntity(dto);
        if (validateDraftPostPublisherExist(post)) {
            logging.log("Draft Post was created successfully", null, "info");
            postRepository.save(post);
        }
        return postMapper.toDto(post);
    }

    public PostDto publishPost(Long draftId) {
        Optional<Post> post = postRepository.findById(draftId);
        if (post.isPresent()) {
            post.get().setPublished(true);
            logging.log("Post with id = {} has been published successfully", draftId, "info");
            postRepository.save(post.get());
            return postMapper.toDto(post.get());
        } else {
            logging.log("Post with id = {} doesn't exist in database", draftId, "error");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setContent(postDto.getContent());
            post.get().setPublished(true);
            postRepository.save(post.get());
            logging.log("Post with id = {} has been updated successfully", postId, "info");
            return postMapper.toDto(post.get());
        } else {
            logging.log("Post with id = {} doesn't exist in database", postId, "error");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto deletePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setDeleted(true);
            postRepository.save(post.get());
            logging.log("Post with id = {} was deleted successfully", postId, "info");
            return postMapper.toDto(post.get());
        } else {
            logging.log("Post with id = {} doesn't exist in database", postId, "error");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            return postMapper.toDto(post.get());
        } else {
            logging.log("Post with id = {} doesn't exist in database", postId, "error");
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
            return sortedList;
        } else {
            logging.log("There's no one post in database written by your publisher", null, "info");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    private boolean validateDraftPostPublisherExist(Post post) {
        boolean result = false;
        if (post.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(post.getId());
            if (userDto.getId() == null) {
                logging.log("User doesn't exist in database", null, "error");
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;

        } else if (post.getProjectId() != null) {
            ProjectDto projectDto = projectServiceClient.getProject(post.getProjectId());
            if (projectDto.getId() == null) {
                logging.log("Project doesn't exist in database", null, "error");
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;
        }
        return result;
    }
}
