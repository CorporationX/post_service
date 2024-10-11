package faang.school.postservice.service;

import faang.school.postservice.model.Post;

import java.util.List;

public interface PostServiceAsync {

    void publishScheduledPostsAsyncInBatch(List<Post> posts);

    void correctUnpublishedPostsByBatches(List<Post> posts);
}
