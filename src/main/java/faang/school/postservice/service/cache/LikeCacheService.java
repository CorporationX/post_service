package faang.school.postservice.service.cache;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.repository.cache.ListCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeCacheService implements SingleCacheService<Long, LikeDto>, MultiGetCacheService<Long, LikeDto> {

    private final ListCacheRepository<LikeDto> listCacheRepository;

    @Override
    public void save(Long postId, LikeDto likeDto) {
        listCacheRepository.rightPush(createKey(postId), likeDto);
    }

    @Override
    public LikeDto get(Long postId) {
        List<LikeDto> likes = getAll(postId);
        return likes.isEmpty() ? null : likes.get(0);
    }

    @Override
    public List<LikeDto> getAll(Long postId) {
        return listCacheRepository.get(createKey(postId));
    }

    private String createKey(Long postId) {
        return postId + "::post_likes";
    }
}
