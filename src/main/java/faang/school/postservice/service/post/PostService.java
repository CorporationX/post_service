package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final PostDataPreparer preparer;
    private final List<PostFilter> postFilters;
    private final CacheService cacheService;
    private final ProducerService producerService;
    private final MessageSource messageSource;

    public PostDto create(PostDto postDto) {
        validator.validateBeforeCreate(postDto);

        Post postEntity = mapper.toEntity(postDto);
        postEntity = preparer.prepareForCreate(postDto, postEntity);

        Post createdPost = postRepository.save(postEntity);
        log.info("Created a post: {}", createdPost);

        if (createdPost.isPublished()) {
            cachePublishedPost(createdPost);
        }

        producerService.sendPostEvent(preparer.createPostEvent(PostEvent.EventType.CREATE, createdPost));

        return mapper.toDto(createdPost);
    }

    public PostDto publish(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validatePublished(entity);
        Post publishedPost = preparer.prepareForPublish(entity);

        publishedPost = postRepository.save(publishedPost);
        cachePublishedPost(publishedPost);
        producerService.sendPostEvent(preparer.createPostEvent(PostEvent.EventType.CREATE, publishedPost));

        return mapper.toDto(publishedPost);
    }


    public PostDto update(PostDto postDto) {
        Post entity = getPostEntity(postDto.getId());
        validator.validateBeforeUpdate(postDto, entity);

        Post updatedPost = preparer.prepareForUpdate(postDto, entity);
        updatedPost = postRepository.save(updatedPost);
        log.info("Updated post: {}", updatedPost);

        cacheService.savePost(mapper.toPostCache(updatedPost));
        producerService.sendPostEvent(preparer.createPostEvent(PostEvent.EventType.UPDATE, updatedPost));

        return mapper.toDto(updatedPost);
    }

    public PostDto delete(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validateDeleted(entity);

        entity.setPublished(false);
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        Post deletedPost = postRepository.save(entity);
        log.info("Deleted post: {}", deletedPost);

        cacheService.deletePost(mapper.toPostCache(deletedPost));
        producerService.sendPostEvent(preparer.createPostEvent(PostEvent.EventType.DELETE, deletedPost));

        return mapper.toDto(deletedPost);
    }

    public PostDto getPost(Long postId) {
        PostDto viewedPostDto = cacheService.getPost(postId)
                .map(mapper::toDto)
                .orElse(null);

        if (viewedPostDto == null) {
            viewedPostDto = mapper.toDto(getPostEntity(postId));
        }

        producerService.sendPostView(new PostViewEvent(viewedPostDto.getId()));
        return viewedPostDto;
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();

        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
    }

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(
                messageSource.getMessage("exception.post_entity_not_found", new Object[]{postId}, Locale.getDefault())));
    }

    private void cachePublishedPost(Post publishedPost) {
        log.info("Published post: {}", publishedPost);

        cacheService.saveUser(publishedPost.getAuthorId());
        cacheService.savePost(mapper.toPostCache(publishedPost));
    }

}
