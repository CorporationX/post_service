package faang.school.postservice.repository;

import faang.school.postservice.model.File;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, Long> {
}
