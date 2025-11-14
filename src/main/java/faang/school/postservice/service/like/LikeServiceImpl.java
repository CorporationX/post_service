package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;

    @Override
    public LikeDto setLikeOnPost(long postId) {
        ResponseEntity<UserDto> userDto = getUserByContextUserId(userContext.getUserId());
        assert userDto.getBody() != null;
        Post post = likeValidator.validateLikeOnPost(postId, userDto.getBody().id(), true);
        Like like = Like.builder()
                .post(post)
                .userId(userDto.getBody().id())
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public void unsetLikeOnPost(long postId) {
        ResponseEntity<UserDto> userDto = getUserByContextUserId(userContext.getUserId());
        assert userDto.getBody() != null;
        likeValidator.validateLikeOnPost(postId, userDto.getBody().id(), false);
        likeRepository.deleteByPostIdAndUserId(postId, userDto.getBody().id());
    }

    @Override
    public LikeDto setLikeOnComment(long commentId) {
        ResponseEntity<UserDto> userDto = getUserByContextUserId(userContext.getUserId());
        assert userDto.getBody() != null;
        Comment comment = likeValidator.validateLikeOnComment(commentId, userDto.getBody().id(), true);
        Like like = Like.builder()
                .comment(comment)
                .userId(userDto.getBody().id())
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public void unsetLikeOnComment(long commentId) {
        ResponseEntity<UserDto> userDto = getUserByContextUserId(userContext.getUserId());
        assert userDto.getBody() != null;
        likeValidator.validateLikeOnComment(commentId, userDto.getBody().id(), false);
        likeRepository.deleteByCommentIdAndUserId(commentId, userDto.getBody().id());
    }

    @Override
    public int getPostLikesCount(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
        return post.getLikes().size();
    }

    private ResponseEntity<UserDto> getUserByContextUserId(long userId) {
        return userServiceClient.getUser(userId);
    }


}
