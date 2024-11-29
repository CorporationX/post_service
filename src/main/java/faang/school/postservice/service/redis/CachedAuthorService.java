package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.redis.CachedAuthorMapper;
import faang.school.postservice.model.redis.CachedAuthor;
import faang.school.postservice.repository.redis.CachedAuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CachedAuthorService {
    private final CachedAuthorRepository repository;
    private final CachedAuthorMapper cachedAuthorMapper;
    private final UserServiceClient userServiceClient;

    public void saveAuthorCache(Long id){
        UserDto author = userServiceClient.getUser(id);
        CachedAuthor authorCache = cachedAuthorMapper.toCachedAuthor(author);
        repository.save(authorCache);
    }
}
