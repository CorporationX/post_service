package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.PostDto;

public interface LikeService {

    PostDto addLikeToPost(Long postId);
    PostDto removeLikeFromPost(Long postId);
    CommentDto addLikeToComment(Long commentId);
    CommentDto removeLikeFromComment(Long commentId);
}
