package faang.school.postservice.service;

import faang.school.postservice.model.Post;

import java.util.List;

public interface AsyncPostPublishService {

    void publishPost(List<Post> posts);
}