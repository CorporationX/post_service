package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostServiceImpl postServiceImpl;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final LikeMapper likeMapper;
    private final KafkaLikeProducer kafkaLikeProducer;

    public LikeDto likePost(LikeDto likeDto) {
        Post post = postServiceImpl.searchPostById(likeDto.getPostId());
        UserDto userDto = getUserFromUserService();
        verifyPostLikeUniqueness(post.getId(), userDto.getId());
        Like like = Like.builder()
                .post(post)
                .userId(userDto.getId())
                .build();

        LikeEventKafka likeEventKafka = new LikeEventKafka(
                likeDto, userDto);
        kafkaLikeProducer.sendMessage(likeEventKafka);
        return likeMapper.toDto(likeRepository.save(like));
    }

    public LikeDto likeComment(LikeDto likeDto) {
        Comment comment = commentService.getCommentIfExist(likeDto.getCommentId());
        UserDto userDto = getUserFromUserService();
        verifyCommentLikeUniqueness(comment.getId(), userDto.getId());
        Like like = Like.builder()
                .comment(comment)
                .userId(userDto.getId())
                .build();
        return likeMapper.toDto(likeRepository.save(like));

    }

    public void deleteLikePost(long postId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByPostIdAndUserId(postId, userDto.getId());
    }

    public void deleteLikeComment(long commentId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByCommentIdAndUserId(commentId, userDto.getId());
    }

    private void verifyPostLikeUniqueness(long postId, long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new DataValidationException("User by id: " + userId + " has already liked the post");
        }
    }

    private void verifyCommentLikeUniqueness(long postId, long userId) {
        if (likeRepository.findByCommentIdAndUserId(postId, userId).isPresent()) {
            throw new DataValidationException("User by id: " + userId + " has already liked the comment");
        }
    }

    private UserDto getUserFromUserService() {
        try {
            return userServiceClient.getUser(userContext.getUserId());
        } catch (FeignException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}

