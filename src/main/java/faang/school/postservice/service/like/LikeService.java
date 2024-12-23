package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.like.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final PostRepository postRepository;
    private final PostService postService;
    private final LikeEventPublisher likeEventPublisher;

    public LikeDto likeComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validateComment(commentId, likeDto);
        likeValidator.validateWhereIsLikePlaced(likeDto);

        Comment comment = commentService.findEntityById(commentId);
        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(comment.getPost());
        comment.getLikes().add(like);

        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    public LikeDto likePost(Long postId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validatePost(postId, likeDto);
        likeValidator.validateWhereIsLikePlaced(likeDto);

        Post post = postService.findEntityById(postId);
        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setComment(null);
        post.getLikes().add(like);

        likeRepository.save(like);

        likeEventPublisher.publish(new LikeEvent(post.getAuthorId(), like.getUserId(), postId));

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto removeLikeUnderComment(Long commentId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validateComment(commentId, likeDto);
        likeValidator.validateLike(likeDto.getId());

        Comment comment = commentService.findEntityById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        likeRepository.deleteByCommentIdAndUserId(commentId, like.getUserId());
        comment.getLikes().removeIf(existingLike -> existingLike.getUserId().equals(like.getUserId()));
        commentRepository.save(comment);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto removeLikeUnderPost(Long postId, LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());
        likeValidator.validatePost(postId, likeDto);
        likeValidator.validateLike(likeDto.getId());

        Post post = postService.findEntityById(postId);
        Like like = likeMapper.toEntity(likeDto);

        likeRepository.deleteByPostIdAndUserId(postId, like.getUserId());
        post.getLikes().removeIf(existingLike -> existingLike.getUserId().equals(like.getUserId()));
        postRepository.save(post);

        return likeMapper.toDto(like);
    }
}
