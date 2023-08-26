package faang.school.postservice.service;

import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentEventPublisher commentEventPublisher;

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Comment with id:%d doesn't exist", commentId)));
    }

    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Transactional
    public Object create() {
        commentEventPublisher.publish(CommentEvent.builder()
                .id(new Random().nextLong(10))
                .authorId(new Random().nextLong(100))
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build());
        return null;
    }
}
