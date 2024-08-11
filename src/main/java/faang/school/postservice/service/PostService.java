package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.ReadPostDto;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.event.PostViewEventPublisher;
import faang.school.postservice.mapper.post.PostToReadPostDtoMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserContext userContext;
    private final PostToReadPostDtoMapper postToReadPostDtoMapper;
    private final PostViewEventPublisher postViewEventPublisher;


    public ReadPostDto findById(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    this.publishViewPostEventIfNotOwnerViewing(post);
                    return post;
                })
                .map(postToReadPostDtoMapper::map)
                .orElseThrow(() -> {
                    log.error("PostService.findById: Post with id %s not found");
                    return new RuntimeException(String.format("Post with id: %s not found", id));
                });
    }

    private void publishViewPostEventIfNotOwnerViewing(Post post) {
        if (post.getAuthorId() != userContext.getUserId()) {
            log.info("PostService.findById: publishes post view event, postId: {}", post.getId());
            postViewEventPublisher.publish(
                    new PostViewEvent(post.getId(),
                            post.getAuthorId(),
                            userContext.getUserId(),
                            LocalDateTime.now()));

        }
    }
}
