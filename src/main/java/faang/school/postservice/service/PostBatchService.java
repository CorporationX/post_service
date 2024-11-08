package faang.school.postservice.service;

import faang.school.postservice.model.entity.Post;

import java.util.List;

public interface PostBatchService {
    void savePostBatch(List<Post> postBatch);
}
