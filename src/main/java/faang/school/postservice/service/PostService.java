package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataDoesNotExistException;
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
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public PostDto createDraftPost(PostDto dto) {
        Post post = postMapper.toEntity(dto);
        if (validateDraftPostPublisher(post)) {
            log.info("Draft Post was created successfully");
            postRepository.save(post);
        }
        return postMapper.toDto(post);
    }

    public PostDto publishPost(Long draftId) {
        Optional<Post> post = postRepository.findById(draftId);
        if (post.isPresent()) {
            post.get().setPublished(true);
            log.info("Post" + draftId + "has been published successfully");
            postRepository.save(post.get());
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = " + draftId + "doesn't exist in database");
            log.error("Post with id = {} doesn't exist in database",  draftId);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setContent(postDto.getContent());
            post.get().setPublished(true);
            postRepository.save(post.get());
            log.info("Post with id = " + post + " was updated successfully");
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", post + "");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto deletePost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setDeleted(true);
            postRepository.save(post.get());
            log.info("Post id = {} was deleted successfully", postId);
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", post);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public PostDto getPost(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            return postMapper.toDto(post.get());
        } else {
            log.error("Post with id = {} doesn't exist in database", post);
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        }
    }

    public List<PostDto> getSortedPosts(PostDto postDto) {
        List<Post> posts = new ArrayList<>();
        List<PostDto> sortedList = new ArrayList<>();
        if (postDto.getAuthorId()!=null){
            posts = postRepository.findByAuthorId(postDto.getAuthorId());
        } else if (postDto.getProjectId()!=null) {
            posts = postRepository.findByProjectId(postDto.getProjectId());
        }
        if (postDto.isPublished()){
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
        if (sortedList.isEmpty()){
            log.info("There's no one eligible post in database");
            throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
        } else {
            return sortedList;
        }
    }

    private boolean validateDraftPostPublisher(Post post) {
        boolean result = false;
        if (post.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(post.getId());
            if (userDto.getId() == null) {
                log.error("User doesn't exist in database");
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;

        } else if (post.getProjectId() != null) {
            ProjectDto projectDto = projectServiceClient.getProject(post.getProjectId());
            if (projectDto.getId() == null) {
                log.error("Project doesn't exist in database");
                throw new DataDoesNotExistException(DOES_NOT_EXIST_IN_DB);
            } else result = true;
        }
        return result;
    }
}
