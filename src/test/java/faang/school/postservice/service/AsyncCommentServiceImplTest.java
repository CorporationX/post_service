package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AsyncCommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModerationDictionaryImpl moderationDictionary;
    @InjectMocks
    private AsyncCommentServiceImpl asyncCommentServiceImpl;

    @Test
    public void moderateCommentsTest() {
        List<Comment> comments = List.of(Comment.builder().content("Test").build());
        when(moderationDictionary.isTextAreCorrect(anyString())).thenReturn(true);

        asyncCommentServiceImpl.moderateComments(comments);

        verify(commentRepository, times(1)).saveAll(anyList());
    }
}
