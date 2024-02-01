package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository <Resource, Long> {

}
