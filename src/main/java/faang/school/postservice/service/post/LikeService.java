package faang.school.postservice.service.post;

import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional
    public Long getNumberOfLike(Long postId) {
        return likeRepository.getNumberOfLikeByPostId(postId);
    }

}
