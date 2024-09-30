package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.kafka.LikeKafkaEvent;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.messaging.publisher.kafka.like.KafkaLikePublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.publisher.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final EventPublisherService eventPublisherService;
    private final KafkaLikePublisher kafkaLikePublisher;

    @Value("${user.batch.size:100}")
    private int batchSize;

    //TODO: Add return type, use mapper
    public void likePost(LikeDto likeDto) {
        Like like = new Like();
        like.setPost(Post.builder().id(likeDto.getPostId()).build());
        like.setComment(Comment.builder().id(likeDto.getCommentId()).build());
        like.setUserId(likeDto.getUserId());
        likeRepository.save(like);

        eventPublisherService.submitEvent(likeDto);
        kafkaLikePublisher.publish(LikeKafkaEvent.builder()
                .authorId(like.getUserId())
                .commentId(like.getComment().getId())
                .postId(like.getPost().getId())
                .build());
    }


    public List<UserDto> getUsersByPostId(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        if (likes.isEmpty()) {
            log.error(ExceptionMessages.LIKE_NOT_FOUND_FOR_POST + ": " + postId);
            throw new NoSuchElementException(ExceptionMessages.LIKE_NOT_FOUND_FOR_POST + ": " + postId);
        }

        List<Long> usersIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        return getUserDtoByBatches(usersIds);
    }

    public List<UserDto> getUsersByCommentId(Long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);
        if (likes.isEmpty()) {
            log.error(ExceptionMessages.LIKE_NOT_FOUND_FOR_COMMENT + ": " + commentId);
            throw new NoSuchElementException(ExceptionMessages.LIKE_NOT_FOUND_FOR_COMMENT + ": " + commentId);
        }

        List<Long> usersIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        return getUserDtoByBatches(usersIds);
    }

    private List<UserDto> getUserDtoByBatches(List<Long> usersIds) {
        List<UserDto> result = new ArrayList<>();
        int batchSize = this.batchSize;
        for (int i = 0; i < usersIds.size(); i += batchSize) {
            int end = Math.min(usersIds.size(), i + batchSize);
            List<Long> batch = usersIds.subList(i, end);
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }
}