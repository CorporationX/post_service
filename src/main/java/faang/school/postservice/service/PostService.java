package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;

    public PostDto createPost(PostDto post) {
        return post;
    }
}
