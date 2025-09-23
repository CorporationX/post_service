package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<User, Long> {

    default User findByIdOrThrow(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id %d not found", userId)));
    }
}
