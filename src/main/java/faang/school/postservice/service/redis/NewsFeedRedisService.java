package faang.school.postservice.service.redis;

import faang.school.postservice.dto.post.SubsDto;
import faang.school.postservice.repository.redis.NewsFeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsFeedRedisService {
    private final NewsFeedRedisRepository newsFeedRedisRepository;

    public void createFeed(SubsDto subsDto){
        newsFeedRedisRepository.createFeed(subsDto);
    }
}
