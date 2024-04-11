package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.kafka_producer.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final LikeServiceValidator likeServiceValidator;
    private final LikeMapper likeMapper;
    private final KafkaLikeProducer kafkaLikeProducer;

    @Value("${like_service.batch}")
    private int likeServiceBatchSize;

    @Transactional
    public LikeDto addLikeToPost(LikeDto likeDto) {
        likeServiceValidator.validateLikeOnPost(likeDto);
        Like likeEntity = likeMapper.toEntity(likeDto);
        Like saved = likeRepository.save(likeEntity);
        kafkaLikeProducer.publishLikeKafkaEvent(saved);
        return likeMapper.toDto(saved);
    }

    @Transactional
    public LikeDto addLikeToComment(LikeDto like) {
        likeServiceValidator.validateLikeOnComment(like);
        Like likeEntity = likeMapper.toEntity(like);
        Like saved = likeRepository.save(likeEntity);
        return likeMapper.toDto(saved);
    }

    @Transactional
    public void deleteLikeFromPost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public void deleteLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPostLikes(long id) {
        List<Like> likes = likeRepository.findByPostId(id);
        List<Long> ids = likes.stream().map(Like::getUserId).toList();

        return retrieveUsersByIds(ids);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getCommentLikes(long id) {
        List<Like> likes = likeRepository.findByCommentId(id);
        List<Long> ids = likes.stream().map(Like::getUserId).toList();

        return retrieveUsersByIds(ids);
    }


    private List<UserDto> retrieveUsersByIds(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>(userIds.size());
        final int totalUserIds = userIds.size();

        for (int startIndex = 0; startIndex < totalUserIds; startIndex += likeServiceBatchSize) {
            int endIndex = Math.min(startIndex + likeServiceBatchSize, totalUserIds);
            List<Long> batchIds = userIds.subList(startIndex, endIndex);

            List<UserDto> batchUsers = userServiceClient.getUsersByIds(batchIds);
            users.addAll(batchUsers);
        }

        return users;
    }


}