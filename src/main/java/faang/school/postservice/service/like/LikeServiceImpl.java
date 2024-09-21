package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.validator.ServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final ServiceValidator validator;
    private final PostRepository postRepository;

    @Transactional
    public LikeDto likeToPost(LikeDto likeDto) {
        validator.validateUserReal(likeDto);
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found"));
        validator.validateDuplicateLikeForPost(likeDto);
        validator.validateLikeToPostAndCommentForPost(likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);
        LikeDto likeDto1 = likeMapper.toDto(like);
        likeDto1.setCommentId(null);
        likeDto1.setPostId(like.getPost().getId());
        return likeDto1;
    }

    @Transactional
    public void unlikeFromPost(LikeDto likeDto) {
        Post post =  validator.validateAndGetPost(likeDto);
        likeRepository.deleteByPostIdAndUserId(post.getId(), likeDto.getUserId());
    }

    @Transactional
    public LikeDto likeToComment(LikeDto likeDto) {
        validator.validateUserReal(likeDto);
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new DataValidationException("Comment not found"));
        validator.validateLikeToPostAndCommentForComment(likeDto);
        validator.validateDuplicateLikeForComment(likeDto);

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(null);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);

        LikeDto likeDto1 = likeMapper.toDto(like);
        likeDto1.setCommentId(like.getComment().getId());
        likeDto1.setPostId(null);
        return likeDto1;
    }

    @Transactional
    public void unlikeFromComment(LikeDto likeDto) {
        Comment comment = validator.validateAndGetComment(likeDto);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), likeDto.getUserId());
    }
}
