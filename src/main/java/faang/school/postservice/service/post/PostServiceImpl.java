package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ServiceUnavailableException;
import faang.school.postservice.kafka.producer.comment.CommentProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.criteria.PostSearchCriteria;
import faang.school.postservice.validation.comment.CommentValidator;
import faang.school.postservice.validation.spellcheck.PostSpellCheckValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final UserFeignService userFeignService;
    private final PostSpellCheckValidator postSpellCheckValidator;

    private final CommentProducer commentProducer;

    @Override
    @Transactional
    public PostDto create(CreatePostDto createPostDto) {
        postValidator.validateCreate(createPostDto);

        Post post = postMapper.toPost(createPostDto);
        postRepository.save(post);

        log.info("Post id: {} created", post.getId());
        return postMapper.toPostDto(post);
    }


    @Override
    @Transactional
    public PostDto publish(@NonNull Long postId) {
        Post post = getNonDeletedPostByIdOrFail(postId);
        postValidator.validatePublish(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);

        log.info("Post id: {} published", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto update(Long postId, UpdatePostDto updatePostDto) {
        Post post = getNonDeletedPostByIdOrFail(postId);
        postValidator.validateUpdate(post);

        postMapper.update(updatePostDto, post);
        postRepository.save(post);

        log.info("Post id: {} updated", post.getId());
        return postMapper.toPostDto(post);
    }

    private Post getNonDeletedPostByIdOrFail(@NonNull Long postId) {
        Optional<Post> optionalPost = postRepository.findByIdAndDeletedFalse(postId);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Post with postId: %d not found.", postId)
            );
        }
        return optionalPost.get();
    }

    @Override
    @Transactional
    public void delete(@NotNull Long postId) {
        Post post = getNonDeletedPostByIdOrFail(postId);

        post.setDeleted(true);
        postRepository.save(post);

        log.info("Post id: {} marked as deleted", post.getId());
    }

    @Override
    public PostDto getById(@NotNull Long postId) {
        return postMapper.toPostDto(getNonDeletedPostByIdOrFail(postId));
    }

    @Override
    public List<PostDto> getDraftsByUser(@NotNull Long userId) {
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .authorId(userId)
                .published(false)
                .deleted(false)
                .sortField(PostSearchCriteria.SortField.CREATED_AT)
                .sortDirection(PostSearchCriteria.SortDirection.DESC)
                .build();

        List<Post> posts = postRepository.findByCriteria(criteria);

        return postMapper.toPostDtoList(posts);
    }

    @Override
    public List<PostDto> getDraftsByProject(@NotNull Long projectId) {
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .projectId(projectId)
                .published(false)
                .deleted(false)
                .sortField(PostSearchCriteria.SortField.CREATED_AT)
                .sortDirection(PostSearchCriteria.SortDirection.DESC)
                .build();

        List<Post> posts = postRepository.findByCriteria(criteria);

        return postMapper.toPostDtoList(posts);
    }

    @Override
    public List<PostDto> getPublishedByUser(@NotNull Long userId) {
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .authorId(userId)
                .published(true)
                .deleted(false)
                .sortField(PostSearchCriteria.SortField.PUBLISHED_AT)
                .sortDirection(PostSearchCriteria.SortDirection.DESC)
                .build();

        List<Post> posts = postRepository.findByCriteria(criteria);

        return postMapper.toPostDtoList(posts);
    }

    @Override
    public List<PostDto> getPublishedByProject(@NotNull Long projectId) {
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .projectId(projectId)
                .published(true)
                .deleted(false)
                .sortField(PostSearchCriteria.SortField.PUBLISHED_AT)
                .sortDirection(PostSearchCriteria.SortDirection.DESC)
                .build();

        List<Post> posts = postRepository.findByCriteria(criteria);

        return postMapper.toPostDtoList(posts);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));
    }

    @Override
    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    @Override
    public CommentDto createComment(Long postId, Long authorId, SaveCommentDto saveCommentDto) {
        userFeignService.getUserOrFail(authorId);
        Post post = getPostById(postId);
        Comment comment = commentMapper.toComment(saveCommentDto);
        comment.setAuthorId(authorId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment id: {} for post id: {} created", savedComment.getId(), postId);
        CommentEvent event = commentMapper.toCommentEvent(savedComment);
        commentProducer.publishCommentEvent(event);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        boolean postExists = existsById(postId);
        commentValidator.ensurePostExists(postExists, postId);
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        log.info("Retrieved {} comments for postId: {}", comments.size(), postId);
        return commentMapper.toCommentDtos(comments);
    }

    @Override
    @Transactional
    public void updatePostContent(Post post, String correctedContent) {
        if (!postSpellCheckValidator.isContentChanged(post, correctedContent)) {
            log.info("Skip update: content unchanged for postId={}", post.getId());
            return;
        }
        post.setContent(correctedContent);
        postRepository.save(post);
        log.info("Post ID {} updated with corrected content", post.getId());
    }

    @Override
    public List<Post> getUnpublishedPosts() {
        return postRepository.findAllByPublishedFalse();
    }
}
