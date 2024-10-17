package faang.school.postservice.service;

import faang.school.postservice.model.entity.Comment;

import java.util.List;

public interface CommentServiceAsync {

    void moderateCommentsByBatches(List<Comment> comments);
}
