package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SpellCheckerService spellCheckerService;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;
    private final PostContextMapper context;

    @Async(value = "threadPool")
    @Transactional
    public void correctPostsContent(List<Post> postList) {
        for (Post post : postList) {
            Optional<String> checkedPostContent = spellCheckerService.checkMessage(post.getContent());
            checkedPostContent.ifPresent(post::setContent);
        }
        postRepository.saveAll(postList);
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        Post post = postMapper.toEntity(postDto);

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> {
                    log.error("Post ID " + postDto.getId() + " not found");
                    return new EntityNotFoundException("Post " + postDto.getId() + " not found");
                });
        postServiceValidator.validateUpdatePost(post, postDto);
        post.setContent(postDto.getContent());

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> {
                    log.error("Post ID " + postDto.getId() + " not found");
                    return new EntityNotFoundException("Post ID " + postDto.getId() + " not found");
                });
        postServiceValidator.validatePublishPost(post, postDto);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post ID " + postId + " not found");
                    return new EntityNotFoundException("Post ID " + postId + " not found");
                });
        postServiceValidator.validateDeletePost(post);
        post.setDeleted(true);
        if (post.isPublished()) {
            post.setPublished(false);
        }

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostDto getPostByPostId(Long postId) {
        return postMapper.toDto(postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post ID " + postId + " not found");
                    return new EntityNotFoundException("Post ID " + postId + " not found");
                }));
    }

    public List<PostDto> getAllDraftPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllDraftPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public Post getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with the same id does not exist"));

        long countLike = post.getLikes().size();
        context.getCountLikeEveryonePost().put(postId, countLike);

        return post;
    }

    private List<Post> sortPostsByCreateAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> sortPostsByPublishAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }
}