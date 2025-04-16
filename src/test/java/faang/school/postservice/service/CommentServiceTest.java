package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.LongStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AsyncCommentServiceImpl asyncCommentServiceImpl;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void moderateCommentsTest() {
        List<Long> commentIds = LongStream.range(1, 101)
                .boxed()
                .toList();

        List<Comment> comments = commentIds.stream()
                .map(id -> Comment.builder().id(id).content("Some content").build())
                .toList();

        when(commentRepository.getUnverifiedCommentsIds()).thenReturn(commentIds);
        when(commentRepository.getUnverifiedComments(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> ids = invocation.getArgument(0);
                    return comments.stream()
                            .filter(c -> ids.contains(c.getId()))
                            .toList();
                });

        commentService.moderateComments(20);

        verify(asyncCommentServiceImpl, times(5)).moderateComments(anyList());
    }
}