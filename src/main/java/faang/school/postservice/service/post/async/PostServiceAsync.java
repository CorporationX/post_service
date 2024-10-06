package faang.school.postservice.service.post.async;

import faang.school.postservice.model.Post;

import java.util.List;

public interface PostServiceAsync {

    void publishScheduledPostsAsyncInBatch(List<Post> posts);
}
