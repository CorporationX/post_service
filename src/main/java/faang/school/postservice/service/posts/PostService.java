package faang.school.postservice.service.posts;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.RequestPostDto;

public interface PostService {
    PostDto create(RequestPostDto requestPostDto);
}