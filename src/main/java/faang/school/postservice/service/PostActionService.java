package faang.school.postservice.service;

import faang.school.postservice.dto.event.PostViewEventDto;
import faang.school.postservice.service.publisher.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostActionService {

    private final PostViewEventPublisher postViewEventPublisher;

    public void registerPostView(Long postId, Long authorId, Long viewerId) {
        if (viewerId == null || (viewerId.equals(authorId))) {
            return;
        }

        PostViewEventDto event = new PostViewEventDto(
                postId, authorId, viewerId, LocalDateTime.now()
        );
        postViewEventPublisher.publish(event);
    }
}
