package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CommentService {

    CommentDto addComment(CommentDto comment);

    void updateComment(long id, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentsByPostId(long postId);

    void deleteComment(long id);

    void verifyCommentsByDate(LocalDateTime verifiedDate) throws IOException, ExecutionException, InterruptedException;
}
