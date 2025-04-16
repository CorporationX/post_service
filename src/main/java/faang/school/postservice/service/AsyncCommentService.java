package faang.school.postservice.service;

import faang.school.postservice.model.Comment;

import java.util.List;

public interface AsyncCommentService {

    void moderateComments(List<Comment> commentList);
}
