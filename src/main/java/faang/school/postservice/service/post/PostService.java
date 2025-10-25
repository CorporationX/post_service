package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.exception.ResourceNotFoundException;

public interface PostService {

    Post getPostById(Long postId) throws ResourceNotFoundException;
}