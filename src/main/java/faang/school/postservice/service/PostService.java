package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;

public class PostService {
    PostRepository postRepository;
    PostServiceValidator postServiceValidator;
    Post getPostById(long id) {
        postServiceValidator.validateForGetPostById(id);
        return null;
    }
}
