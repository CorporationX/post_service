package faang.school.postservice.service.like.implementations;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.interfaces.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;

    /**
     * @param likeDto 
     * @return
     */
    @Override
    public LikeDto likePost(LikeDto likeDto) {
        Like like = likeMapper.toEntity(likeDto);
        return likeMapper.toDto(likeRepository.save(like));
    }

    /**
     * @param likeDto 
     */
    @Override
    public void unlikePost(LikeDto likeDto) {

    }

    /**
     * @param likeDto 
     * @return
     */
    @Override
    public LikeDto likeComment(LikeDto likeDto) {
        return null;
    }

    /**
     * @param likeDto 
     */
    @Override
    public void unlikeComment(LikeDto likeDto) {

    }
}
