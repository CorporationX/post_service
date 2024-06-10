package faang.school.postservice.service.post;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

public interface PostService {
    void moderatePosts();
    Post getPostById(long id);
    Post createPost(PostDto postDto);
    void publishScheduledPosts();
}
