package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true,
            value = """
                    select id from users
                    where active = true;
                    """
    )
    List<Long> getActiveUsersId();
}
