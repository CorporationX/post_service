package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer> {

    @Query("SELECT pr FROM Resource pr WHERE pr.post.id = :postId")
    List<Resource> findByPostId(long postId);

    Optional<Object> findById(@Positive(message = "Id must be a positive number") Long id);

    void deleteById(@Positive(message = "Id must be a positive number") Long id);
}
