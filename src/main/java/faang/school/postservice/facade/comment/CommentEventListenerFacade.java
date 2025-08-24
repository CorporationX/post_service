package faang.school.postservice.facade.comment;


import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.model.cache.CommentCacheModel;
import faang.school.postservice.service.post.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventListenerFacade {

    private final PostCacheService postCacheService;

    public void saveCommentInCache(CommentEvent commentEvent) {
        CommentCacheModel cacheModel = CommentCacheModel.builder()
                .id(commentEvent.getId())
                .userId(commentEvent.getAuthorId())
                .content(commentEvent.getContent())
                .createdAt(commentEvent.getCratedAt())
                .build();

        postCacheService.addComment(cacheModel, commentEvent.getPostId());
    }
}
