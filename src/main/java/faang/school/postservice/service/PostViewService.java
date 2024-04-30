package faang.school.postservice.service;

import faang.school.postservice.dto.PostViewDto;
import faang.school.postservice.dto.event.PostViewAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostViewMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostView;
import faang.school.postservice.publisher.kafka.KafkaEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class PostViewService {

    private final PostViewRepository postViewRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PostViewMapper postViewMapper;
    private final KafkaEventPublisher kafkaEventPublisher;
    @Value("${spring.kafka.topics.post-views}")
    private String postViewsTopic;

    @Transactional
    public PostViewDto addPostView(PostViewDto postViewDto) {
        long postId = postViewDto.getPostId();

        Post post = postService.getPost(postId);

        PostView postView = postViewMapper.toEntity(postViewDto);
        postView.setPost(post);

        PostView savedPostView = postViewRepository.save(postView);

        post.getPostViews().add(savedPostView);
        postRepository.save(post);

        PostViewAddEvent postViewAddEvent = new PostViewAddEvent(
                savedPostView.getId(),
                savedPostView.getViewerId(),
                postId,
                LocalDateTime.now()
        );

        kafkaEventPublisher.sendEvent(postViewsTopic, postViewAddEvent);
        return postViewMapper.toDto(savedPostView);
    }

    public PostView getPostView(long postViewId) {
        return postViewRepository.findById(postViewId).orElseThrow(() ->
                new NotFoundException(String.format("PostView not found by Id: %d", postViewId)));
    }
}
