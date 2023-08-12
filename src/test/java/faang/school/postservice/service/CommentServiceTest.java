package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    ModerationDictionary moderationDictionary;

    @InjectMocks
    CommentService commentService;

    @Test
    public void testModerateComments() {
        commentService.setBatchSize(50);
        Mockito.when(commentRepository.findUnverifiedComments()).thenReturn(List.of(new Comment()));
        commentService.moderateComments();

        Mockito.verify(commentRepository).findUnverifiedComments();
        Mockito.verify(moderationDictionary).checkComment(Mockito.any());
        Mockito.verify(commentRepository).saveAll(Mockito.any());
    }
}