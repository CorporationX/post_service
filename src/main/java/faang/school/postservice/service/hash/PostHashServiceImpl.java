package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.hash.PostHash;
import faang.school.postservice.repository.PostHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class PostHashServiceImpl implements PostHashService{
    private final PostHashRepository postHashRepository;

    @Value("${feed.post.time-to-live}")
    private long ttl;
    @Value("${feed.comment.size}")
    private int commentSize;

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${feed.maxAttempts}")
    public void savePost(PostEventKafka postEvent) {
        PostHash postHash = new PostHash();

        postHash.setTtl(ttl);
        postHash.setContent(postEvent.getContent());
        postHash.setAuthorId(postHash.getAuthorId());
        postHash.setPostId(postEvent.getPostId());
        postHash.setProjectId(postHash.getProjectId());
        postHash.setVersion(postHash.getVersion());
        postHash.setPublishedAt(postEvent.getPublishedAt());

        postHashRepository.save(postHash);
    }

    public void addComment(CommentEventKafka commentEvent) {
        postHashRepository.findById(commentEvent.getPostId())
                .ifPresent(postHash -> {
                   postHash.getComments().add(commentEvent);
                   checkCommentSize(postHash);
                   postHashRepository.save(postHash);
                });
    }

    private void checkCommentSize(PostHash postHash) {
        while (postHash.getComments().size() > commentSize) {
            Iterator<CommentEventKafka> iterator = postHash.getComments().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            } else {
                break;
            }
        }
    }
}
