package faang.school.postservice.service.comment.async;

import faang.school.postservice.model.Comment;

import java.util.List;

public interface CommentServiceAsync {

    void moderateCommentsByBatches(List<Comment> comments);
}
