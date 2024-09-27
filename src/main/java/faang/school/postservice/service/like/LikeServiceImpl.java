package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final ServiceValidator validator;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());
        validator.validateUserReal(likeDto, userDto);
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found"));
        List<Like> postLikes = likeRepository.findByPostId(likeDto.getPostId());
        validator.validateDuplicateLikeForPost(likeDto, postLikes);
        validateLikeToPostAndCommentForPost(likeDto);

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
    public void unlikePost(LikeDto likeDto) {
        Post post =  validateAndGetPost(likeDto);
        likeRepository.deleteByPostIdAndUserId(post.getId(), likeDto.getUserId());
    }

    @Transactional
    public LikeDto likeComment(LikeDto likeDto) {
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());
        validator.validateUserReal(likeDto, userDto);
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new DataValidationException("Comment not found"));
        validateLikeToPostAndCommentForComment(likeDto);
        List<Like> commentLikes = likeRepository.findByCommentId(likeDto.getCommentId());
        validator.validateDuplicateLikeForComment(likeDto, commentLikes);

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
    public void unlikeComment(LikeDto likeDto) {
        Comment comment = validateAndGetComment(likeDto);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), likeDto.getUserId());
    }

    private void validateLikeToPostAndCommentForComment(LikeDto likeDto) {
        List<Long> postLikes =  postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found"))
                .getLikes()
                .stream()
                .map(Like::getUserId)
                .toList();

        if (postLikes.contains(likeDto.getUserId())) {
            throw new DataValidationException("Like already exist on the post!");
        }
    }

    private void validateLikeToPostAndCommentForPost(LikeDto likeDto) {
        List<Long> commentsLikes = postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found"))
                .getComments()
                .stream()
                .flatMap(comment -> comment.getLikes().stream())
                .map(Like::getUserId)
                .toList();

        if (commentsLikes.contains(likeDto.getUserId())) {
            throw new DataValidationException("Like already exist on the comment!");
        }
    }

    private Post validateAndGetPost(LikeDto likeDto) {
        List<Post> postsUser = postRepository.findByAuthorIdWithLikes(likeDto.getUserId());
        return postsUser
                .stream()
                .filter(filter -> filter.getId() == likeDto.getPostId())
                .findFirst()
                .orElseThrow(() -> new DataValidationException("post not found in getPost"));
    }

    private Comment validateAndGetComment(LikeDto likeDto) {
        List<Comment> comments = commentRepository.findAllByPostId(likeDto.getPostId());
        return comments
                .stream()
                .filter(filter -> filter.getId() == likeDto.getCommentId())
                .findFirst()
                .orElseThrow(() -> new DataValidationException("comment not found in getComment"));
    }
}
