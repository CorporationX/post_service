package faang.school.postservice.service;

import faang.school.postservice.model.entity.Post;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostBatchService {
    void savePostBatch(List<Post> postBatch);
}
