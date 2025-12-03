package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostPublishedEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.PostEventProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.client.ProjectServiceClientAdapter;
import faang.school.postservice.util.client.UserServiceClientAdapter;
import faang.school.postservice.util.post.PostRepositoryAdapter;
import faang.school.postservice.util.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final UserServiceClientAdapter userServiceClientAdapter;
    private final ProjectServiceClientAdapter projectServiceClientAdapter;
    private final UserContext userContext;
    private final PostEventProducer postEventProducer;

    @Override
    @Transactional
    public PostDto createDraft(PostDto postDto) {
        defineUserOrProject(postDto);
        Post post = postMapper.toPost(postDto);
        postValidator.validatePostIsPublished(post);
        postValidator.validatePostIsDeleted(post);
        post = postRepository.save(post);
        log.info("Draft #{} is created", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto publishPost(long postId) {
        Post post = postRepositoryAdapter.getPostById(postId);
        defineUserOrProject(postMapper.toPostDto(post));
        postValidator.validatePostIsPublished(post);
        postValidator.validatePostIsDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        log.info("Post #{} is published", postId);
        postEventProducer.sendPostPublishedEvent(
                new PostPublishedEvent(
                        post.getId(),
                        post.getAuthorId(),
                        post.getContent(),
                        post.getPublishedAt()
                ));
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(long postId, PostDto postDto) {
        defineUserOrProject(postDto);
        Post currentPost = postRepositoryAdapter.getPostById(postId);
        postValidator.validatePostIsDeleted(currentPost);
        postValidator.validateChangeAuthor(currentPost, postDto);
        postMapper.updatePost(currentPost, postDto);
        log.info("Post #{} is updated", postId);
        return postMapper.toPostDto(currentPost);
    }

    @Override
    @Transactional
    public PostDto deletePost(long postId) {
        Post post = postRepositoryAdapter.getPostById(postId);
        defineUserOrProject(postMapper.toPostDto(post));
        post.setDeleted(true);
        post = postRepository.save(post);
        log.info("Post #{} is deleted", postId);
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto findPostById(long postId) {
        Post post = postRepositoryAdapter.getPostById(postId);
        postValidator.validatePostIsUnpublished(post);
        postValidator.validatePostIsDeleted(post);
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public List<PostDto> findDraftsByAuthorId(long authorId) {
        return postRepository.findByPublishedFalseAndAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findDraftsByProjectId(long projectId) {
        return postRepository.findByPublishedFalseAndProjectIdAndDeletedFalseOrderByCreatedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findPostsByAuthorId(long authorId) {
        return postRepository.findByPublishedTrueAndAuthorIdAndDeletedFalseOrderByPublishedAtDesc(authorId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findPostsByProjectId(long projectId) {
        return postRepository.findByPublishedTrueAndProjectIdAndDeletedFalseOrderByPublishedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    private void defineUserOrProject(PostDto postDto) {
        long currentUserId = userContext.getUserId();
        userServiceClientAdapter.getUserById(currentUserId);
        if (postDto.projectId() == null) {
            postValidator.validateUser(currentUserId, postDto);
        } else {
            long ownerId = projectServiceClientAdapter.getProjectById(postDto.projectId()).ownerId();
            postValidator.validateProject(ownerId, currentUserId, postDto.projectId());
        }
    }
}
