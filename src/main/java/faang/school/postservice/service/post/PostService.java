package faang.school.postservice.service.post;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.SortField;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostFilterRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final List<PostFilterRepository> postFilterRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostPublishService postPublishService;
    @Value("${post.publisher.batch-size}")
    private int postsBatchSize;

    public void publishScheduledPosts() {
        log.info("Start publishing posts, at: {}", LocalDateTime.now());
        List<Post> posts = postRepository.findReadyToPublish();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < posts.size(); i += postsBatchSize) {
            int end = Math.min(i + postsBatchSize, posts.size());
            List<Post> batch = posts.subList(i, end);
            CompletableFuture<Void> future = postPublishService.publishBatch(batch)
                    .thenAccept(postRepository::saveAll);
            futures.add(future);
        }
        CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFutures.join();
        log.info("All posts successful published, at: {}", LocalDateTime.now());
    }

    @Transactional
    public PostDto create(@Valid @Nonnull PostCreateDto postCreateDto) {
        postValidator.checkIfPostHasAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());

        Post createdPost = postRepository.save(postMapper.toPost(postCreateDto));
        return postMapper.toDto(createdPost);
    }

    @Transactional
    public PostDto publish(Long id) {
        Post post = getEntityById(id);
        postValidator.checkPostPublished(post.getId(), post.isPublished());
        post.setPublished(true);

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto update(@Valid @Nonnull PostUpdateDto postDto) {
        postValidator.validateForUpdating(postDto);

        Post postToUpdate = getEntityById(postDto.getId());
        postToUpdate.setContent(postDto.getContent());

        return postMapper.toDto(postRepository.save(postToUpdate));
    }

    @Transactional
    public void delete(Long id) {
        Post postToDelete = getEntityById(id);

        postRepository.save(postToDelete);
    }

    @Transactional
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));
        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public Post validationAndPostReceived(LikeDto likeDto) {
        if (likeDto.getPostId() != null) {
            if (!postRepository.existsById(likeDto.getPostId())) {
                throw new DataValidationException("no such postId exists postId: " + likeDto.getPostId());
            }
        } else {
            throw new DataValidationException("arrived likeDto with postId equal to null");
        }
        return postRepository.findById(likeDto.getPostId()).orElseThrow(() ->
                new NotFoundEntityException("Not found post by id: " + likeDto.getPostId()));
    }

    @Transactional
    public Post getEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Post %s doesn't exist", id)));
    }

    @Transactional
    public Page<PostDto> getPostsByPublishedStatus(PostFilterDto postFilter) {
        Optional<Specification<Post>> postSpecification = postFilterRepository.stream()
                .filter(filter -> filter.isApplicable(postFilter))
                .map(filter -> filter.apply(postFilter))
                .reduce(Specification::and);

        postSpecification.orElseThrow(() -> new DataValidationException("Required fields are incorrect"));

        Pageable pageRequest = postFilter.getPublished()
                ? PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.PUBLISHED_AT.getValue()).descending())
                : PageRequest.of(postFilter.getPage(), postFilter.getSize(), Sort.by(SortField.CREATED_AT.getValue()).descending());

        return postRepository.findAll(postSpecification.get(), pageRequest).map(postMapper::toDto);
    }
}
