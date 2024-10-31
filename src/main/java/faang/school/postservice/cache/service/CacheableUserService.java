package faang.school.postservice.cache.service;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheableUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheableUserService {
    private final CacheableUserRepository cacheableUserRepository;

    public List<CacheableUser> getAllByIds(Iterable<Long> ids) {
        Iterable<CacheableUser> cacheableUserIterable = cacheableUserRepository.findAllById(ids);
        return StreamSupport.stream(cacheableUserIterable.spliterator(), false)
                .toList();
    }

    public void save(UserDto userDto) {
        if (!existsById(userDto.getId())) {
            CacheableUser cacheableUser = new CacheableUser(userDto.getId(), userDto.getUsername());
            save(cacheableUser);
        }
    }

    public void saveAll(Iterable<CacheableUser> cacheableUsers) {
        cacheableUserRepository.saveAll(cacheableUsers);
    }

    public boolean existsById(Long id) {
        return cacheableUserRepository.existsById(id);
    }

    private void save(CacheableUser cacheableUser) {
        cacheableUserRepository.save(cacheableUser);
        log.info("User by id {} saved to cache", cacheableUser.getId());
    }
}
