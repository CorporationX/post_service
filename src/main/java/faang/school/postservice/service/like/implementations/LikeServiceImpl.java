package faang.school.postservice.service.like.implementations;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentIdMismatchException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.interfaces.LikeService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public LikeDto likePost(long postId, LikeDto likeDto) {
        Post post = checkLikeDToWithPostId(postId, likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        return likeMapper.toDto(likeRepository.save(like));
    }

    private Post checkLikeDToWithPostId(long postId, LikeDto likeDto) {
        if (postId != likeDto.getPostId()) {
            log.error("Post ID mismatch: path={}, dto={}", postId, likeDto.getPostId());
            throw new PostIdMismatchException("Post ID in path and DTO must match");
        }
        checkAuthor(likeDto.getUserId());
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    @Override
    public void unlikePost(long postId, LikeDto likeDto) {
        checkLikeDToWithPostId(postId, likeDto);
        if (likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isPresent()) {
            likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
        }
    }

    @Override
    public LikeDto likeComment(long commentId, LikeDto likeDto) {
        Comment comment = checkLikeDtoWithCommentId(commentId, likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        return likeMapper.toDto(likeRepository.save(like));
    }

    private Comment checkLikeDtoWithCommentId(long commentId, LikeDto likeDto) {
        if (commentId != likeDto.getCommentId()) {
            log.error("Comment ID mismatch: path={}, dto={}", commentId, likeDto.getCommentId());
            throw new CommentIdMismatchException("Comment ID in path and DTO must match");
        }
        checkAuthor(likeDto.getUserId());
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    @Override
    public void unlikeComment(long commentId, LikeDto likeDto) {
        checkLikeDtoWithCommentId(commentId, likeDto);
        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()).isPresent()) {
            likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
        }
    }

    private void checkAuthor(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            log.error("Author not found: id={}", authorId, e);
            throw new AuthorNotFoundException("Author with id " + authorId + " not found");
        }
    }

}
