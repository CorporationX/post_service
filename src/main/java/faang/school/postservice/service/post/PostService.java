package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;

public interface PostService {
    PostDto getPost(Long postId);
}
