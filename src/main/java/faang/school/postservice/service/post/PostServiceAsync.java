package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;

import java.util.List;

public interface PostServiceAsync {

    void verifyPosts(List<Post> posts);
}
