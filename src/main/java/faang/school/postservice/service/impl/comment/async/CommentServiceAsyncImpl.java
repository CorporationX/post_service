package faang.school.postservice.service.impl.comment.async;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentServiceAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceAsyncImpl implements CommentServiceAsync {

    private final ModerationDictionary dictionary;
    private final CommentRepository commentRepository;

    @Override
    @Async("fixedThreadPool")
    public void moderateCommentsByBatches(List<Comment> comments) {
        comments.forEach(comment -> {
            boolean badWordsExist = dictionary.containsBadWords(comment.getContent());

            comment.setVerified(!badWordsExist);
            comment.setVerifiedDate(LocalDateTime.now());
        });

        commentRepository.saveAll(comments);
    }
}