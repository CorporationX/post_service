package faang.school.postservice.moderation.moderator.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.comment.CommentModerator;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommentModeratorTest {
    @InjectMocks
    private CommentModerator commentModerator;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ModerationDictionary moderationDictionary;

    @Test
    @Scheduled(cron = "0 0 8 * * *")
    void verifyCommentsByDateTest() throws IOException {
        int batchSize = 100;
        LocalDateTime verifiedDate = LocalDateTime.now();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        comment2.setVerifiedDate(verifiedDate);
        comment1.setVerifiedDate(verifiedDate);
        List<Comment> listOfComments = List.of(comment1, comment2);
        when(commentRepository.findCommentsByVerifiedDate(verifiedDate)).thenReturn(listOfComments);

        commentModerator.verifyCommentsByDate(verifiedDate);

        verify(moderationDictionary, times(2)).verifyComment(comment1);
        verify(moderationDictionary, times(2)).verifyComment(comment2);


    }
}
