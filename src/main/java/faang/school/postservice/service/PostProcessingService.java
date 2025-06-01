package faang.school.postservice.service;

import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.feed.PostPublishEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.AuthorRequestEventPublisher;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostProcessingService {

    private final PostRepository postRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostEventPublisher postEventPublisher;
    private final AuthorRequestEventPublisher authorRequestEventPublisher;
    private final PostMapper postMapper;
    private final ApplicationContext applicationContext;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishBatch(List<Post> batch) {
        if (batch == null || batch.isEmpty()) {
            log.error("в списке не содержится постов");
            throw new DataValidationException("список постов пуст");
        }

        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }

        postRepository.saveAll(batch);
        log.info("Опубликовано {} постов с {} по {} id.",
                batch.size(), batch.get(0).getId(), batch.get(batch.size() - 1).getId());
    }

    @Async("postPublisherExecutor")
    public void processPostsAfterPublish(List<PostResponseDto> postDtoList) {
        PostProcessingService proxy = applicationContext.getBean(PostProcessingService.class);
        postDtoList.forEach(proxy::processPostAfterPublish);
    }

    @Async("postPublisherExecutor")
    public void processPostAfterPublish(PostResponseDto postDto) {
        try {
            PostRedisDto postRedisDto = postMapper.toRedisDto(postDto);
            postRedisRepository.savePost(postRedisDto);
            PostPublishEvent postPublishEvent = postMapper.toPublishEvent(postRedisDto);
            postEventPublisher.publish(postPublishEvent);
            authorRequestEventPublisher.publish(postPublishEvent.authorId());
        } catch (Exception e) {
            log.error("Failed to process post ID {}: {}\n{}", postDto.getId(), e.getMessage(), e.getStackTrace());
        }
    }
}
