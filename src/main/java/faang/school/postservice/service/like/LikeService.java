package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeDto createLikeForPost (long postId, LikeDto likeDto) {

    }



}
