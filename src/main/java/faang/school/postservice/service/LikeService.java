package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final PostRepository postRepository;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Post with id:%d doesn't exist", likeDto.getPostId())));
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Comment with id:%d doesn't exist", likeDto.getCommentId())));
        Like like = likeMapper.toModel(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
        log.info("Comment id={} was liked by user id={}", likeDto.getCommentId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Comment id={} was unliked by user id={}", commentId, userId);
    }
}
