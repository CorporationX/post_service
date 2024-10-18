package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.redisCache.PostCache;
import faang.school.postservice.dto.redisCache.UserCache;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.redisCache.RedisPostRepository;
import faang.school.postservice.repository.redisCache.RedisUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CacheService {

  private final RedisPostRepository redisPostRepository;
  private final RedisUserRepository redisUserRepository;
  private final UserServiceClient userServiceClient;
  private final long ttl;

  public CacheService(RedisPostRepository postCacheRepository,
                      RedisUserRepository userRepository,
                      UserServiceClient userServiceClient,
                      @Value("${spring.data.redis.cache.post.ttl:86400}") long ttl) {
    this.redisUserRepository = userRepository;
    this.redisPostRepository = postCacheRepository;
    this.userServiceClient = userServiceClient;
    this.ttl = ttl;
  }

  public void savePost(PostCache postCache) {
    postCache.setTtl(ttl);
    redisPostRepository.save(postCache);
  }

  public void deletePost(PostCache postCache) {
    redisPostRepository.delete(postCache);
  }

  public Optional<PostCache> getPost(Long postId) {
    return redisPostRepository.findById(postId);
  }

  public void saveUser(Long userId) {
    UserDto userDto = userServiceClient.getUser(userId);
    UserCache userCache = new UserCache(userDto.getId(), userDto.getUsername(), ttl);

    redisUserRepository.save(userCache);
  }
}
