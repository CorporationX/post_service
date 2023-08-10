package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeServiceValidator likeServiceValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    public LikeDto addLikeToPost(long postId, LikeDto like) {
        likeServiceValidator.validateLikeOnPost(postId, like);
        Like likeEntity = likeMapper.toEntity(like);
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public LikeDto addLikeToComment(long commentId, LikeDto like) {
        likeServiceValidator.validateLikeOnComment(commentId, like);
        Like likeEntity = likeMapper.toEntity(like);
        return likeMapper.toDto(likeRepository.save(likeEntity));
    }

    public void deleteLikeFromPost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public void deleteLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
