package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public LikeDto addLikeToPost(long postId, LikeDto like){
        Like likeEntity = likeMapper.toEntity(like);
        Optional<Post> post = postRepository.findById(postId);
        post.ifPresent(likeEntity::setPost);
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public LikeDto addLikeToComment(long commentId,LikeDto like){
        Like likeEntity = likeMapper.toEntity(like);
        Optional<Comment> comment = commentRepository.findById(commentId);
        comment.ifPresent(likeEntity::setComment);
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public void deleteLikeFromPost(long postId, long userId){
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public void deleteLikeFromComment(long commentId, long userId){
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
