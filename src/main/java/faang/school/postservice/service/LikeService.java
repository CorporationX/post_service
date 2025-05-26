package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CreatePostDto;

public interface LikeService {

    CreatePostDto addLikeToPost(Long postId);
    CreatePostDto removeLikeFromPost(Long postId);
    CommentDto addLikeToComment(Long commentId);
    CommentDto removeLikeFromComment(Long commentId);
}
