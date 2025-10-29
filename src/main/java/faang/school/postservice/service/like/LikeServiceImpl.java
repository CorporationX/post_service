package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikeValidatorImpl likeValidator;
    private final LikeMapper likeMapper;

    @Override
    public LikeDto setLikeOnPost(long postId) {
        UserDto userDto = getUserByContextUserId(userContext.getUserId());
        Post post = likeValidator.validateLikeOnPost(postId, userDto.id(), true);
        Like like = Like.builder()
                .post(post)
                .userId(userDto.id())
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public void unsetLikeOnPost(long postId) {
        UserDto userDto = getUserByContextUserId(userContext.getUserId());
        likeValidator.validateLikeOnPost(postId, userDto.id(), false);
        likeRepository.deleteByPostIdAndUserId(postId, userDto.id());
    }

    @Override
    public LikeDto setLikeOnComment(long commentId) {
        UserDto userDto = getUserByContextUserId(userContext.getUserId());
        Comment comment = likeValidator.validateLikeOnComment(commentId, userDto.id(), true);
        Like like = Like.builder()
                .comment(comment)
                .userId(userDto.id())
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public void unsetLikeOnComment(long commentId) {
        UserDto userDto = getUserByContextUserId(userContext.getUserId());
        likeValidator.validateLikeOnComment(commentId, userDto.id(), false);
        likeRepository.deleteByCommentIdAndUserId(commentId, userDto.id());
    }

    @Override
    public int getPostLikesCount(long postId) {
        AtomicInteger likeCounts = new AtomicInteger(0);
        postRepository.findById(postId)
                .ifPresent(post -> likeCounts.set(post.getLikes().size()));
        return likeCounts.get();
    }

    private UserDto getUserByContextUserId(long userId) {
        return userServiceClient.getUser(userId);
    }


}
