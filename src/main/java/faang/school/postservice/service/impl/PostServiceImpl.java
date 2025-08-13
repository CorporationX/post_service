package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.NotExistsException;
import faang.school.postservice.exception.NotSupportedDataException;
import faang.school.postservice.integration.project.service.ProjectServiceClient;
import faang.school.postservice.integration.user.service.UserServiceClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public PostDto findById(long id) {
        return postRepository.findById(id)
                .map(p -> postMapper.postToPostDto(p))
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + id + " не найден"));
    }

    @Override
    public void save(PostDraftDto postDraftDto) {
        postRepository.save(postMapper.postDraftDtoToPost(postDraftDto));
    }

    @Override
    public PostDto markPostAsDeleted(long id) {
        Post post = findPostEntityById(id);
        post.setDeleted(true);
        post.setPublished(false);
        return postMapper.postToPostDto(postRepository.save(post));
    }

    @Override
    public PostDto createPostDraft(PostDraftDto postDraftDto) {
        checkAuthorExists(postDraftDto);
        Post post = postMapper.postDraftDtoToPost(postDraftDto);
        post.setPublished(false);
        post.setDeleted(false);
        return postMapper.postToPostDto(postRepository.save(post));
    }

    @Override
    public void publishPost(Long postId, PostDraftDto postDraftDto) {
        Post post;
        if (postId == null) {
            if (postDraftDto == null) {
                throw new NotSupportedDataException("Если postId = null, то postDraftDto != null");
            }
            checkAuthorExists(postDraftDto);
            post = postMapper.postDraftDtoToPost(postDraftDto);
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            post.setDeleted(false);
        } else {
            post = findPostEntityById(postId);
            if (post.isPublished()) {
                throw new NotSupportedDataException("Нельзя опубликовать пост повторно");
            }
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        postRepository.save(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long postId) {
        Post post = findPostEntityById(postId);
        post.setContent(postDto.getContent());
        postRepository.save(post);
        return postMapper.postToPostDto(post);
    }

    @Override
    public List<PostDto> getAllPostsByAuthorId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthorId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    private void checkAuthor(PostDraftDto postDraftDto) {
        if (postDraftDto.getProjectId() == null) {
            if (postDraftDto.getAuthorId() == null) {
                throw new NotSupportedDataException("Необходимо укзать одно из этих полей: AuthorId или ProjectId");
            }
        }
    }

    private void checkAuthorExists(PostDraftDto postDraftDto) {
        checkAuthor(postDraftDto);
        if (postDraftDto.getAuthorId() != null) {
            if (userServiceClient.getUser(postDraftDto.getAuthorId()) == null) {
                throw new NotExistsException("Автор с id " + postDraftDto.getAuthorId() + " не найден в базе");
            }
        } else {
            if (projectServiceClient.getProject(postDraftDto.getProjectId()) == null) {
                throw new NotExistsException("Проект с id " + postDraftDto.getProjectId() + " не найден в базе");
            }
        }
    }

    private Post findPostEntityById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + postId + " не найден"));
    }
}
