package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.entity.Post;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public PostDto createDraftPost(PostDto postDto) {
        postValidator.createDraftPostValidator(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

        postValidator.publishPostValidator(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

        postValidator.updatePostValidator(post, postDto);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto softDeletePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.id())
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postDto.id()));

        post.setPublished(false);
        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + id));

        return postMapper.toDto(post);
    }

    @Transactional
    public List<PostDto> getAllDraftsByAuthorId(Long authorId) {
        postValidator.validateIfAuthorExists(authorId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getAuthorId(), authorId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
        public List<PostDto> getAllPublishedPostsByAuthorId(Long authorId) {
        postValidator.validateIfAuthorExists(authorId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getAuthorId(), authorId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        postValidator.validateIfProjectExists(projectId);

        List<PostDto> posts = StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> Objects.equals(post.getProjectId(), projectId))
                .filter(Post::isPublished)
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();

        return posts;
    }
}
