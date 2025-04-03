package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.LikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final static int BATCH_SIZE = 100;

    private final UserService userService;
    private final UserServiceClient userServiceClient;

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeProducer likeProducer;

    @Transactional
    public LikeDto createPostLike(LikeDto dto) {
        likeValidator.validateLikeCreationParams(dto);
        Like entity = likeMapper.toEntity(dto);
        entity.setComment(null);
        likeProducer.publish(
                likeMapper.toEvent(likeRepository.save(entity))
        );

        return likeMapper.toDto(entity);
    }

    @Transactional
    public void removePostLike(LikeDto dto) {
        userService.getUserDtoById(dto.userId());
        likeValidator.checkLikeBeforeDelete(dto);
        likeRepository.deleteLikeByPostIdAndUserId(dto.postId(), dto.userId());
    }

    @Transactional
    public LikeDto createCommentLike(LikeDto dto) {
        likeValidator.validateLikeCreationParams(dto);
        Like like = likeMapper.toEntity(dto);
        likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    @Transactional
    public void removeCommentLike(LikeDto dto) {
        userService.getUserDtoById(dto.userId());
        likeValidator.checkLikeBeforeDelete(dto);
        likeRepository.deleteLikeByCommentIdAndUserId(dto.commentId(), dto.userId());
    }

    public List<UserDto> getAllUsersWhoLikedPost(Long postId) {
        return getUserDto(likeRepository
                .findAllByPostId(postId).stream()
                .map(Like::getUserId)
                .toList());
    }

    public List<UserDto> getAllUsersWhoLikedComment(Long commentId) {
        return getUserDto(likeRepository
                .findAllByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList());
    }

    private List<UserDto> getUserDto(List<Long> usersId) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < usersId.size(); i += BATCH_SIZE) {
            List<Long> batch = usersId.subList(i, Math.min(i + BATCH_SIZE, usersId.size()));
            batches.add(batch);
        }
        return collectUsersDto(batches);
    }

    private List<UserDto> collectUsersDto(List<List<Long>> batches) {
        List<UserDto> userDto = new ArrayList<>();
        for (List<Long> batch : batches) {
            userDto.addAll(userServiceClient.getUsersByIds(batch));
        }
        return userDto;
    }
}
