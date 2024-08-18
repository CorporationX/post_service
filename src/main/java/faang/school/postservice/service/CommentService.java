package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;
    @Value("${comment.moderator.count-comments-in-thread}")
    public int countPostsInThread;

    public void moderateComments() {
        List<Comment> comments = commentRepository.findNotVerified();
        if (comments.isEmpty()) {
            return;
        }
        for (int i = 0; i < comments.size(); i += countPostsInThread) {
            if (i + countPostsInThread > comments.size()) {
                verifyComments(comments.subList(i, comments.size()));
            } else
                verifyComments(comments.subList(i, i + countPostsInThread));
        }
    }

    @Async
    public void verifyComments(List<Comment> comments) {
        Set<String> banWords = moderationDictionary.getBadWords();
        for (Comment comment : comments) {
            Optional<String> foundBanWord = banWords.stream()
                    .filter(x -> comment.getContent().toLowerCase().contains(x))
                    .findFirst();

            if (foundBanWord.isPresent()) {
                log.info("Comment with id {} contains banned word {}", comment.getId(), foundBanWord.get());
                comment.setVerified(false);
            } else {
                comment.setVerified(true);
                comment.setVerifiedDate(LocalDateTime.now());
            }
            commentRepository.save(comment);
        }
    }
}

