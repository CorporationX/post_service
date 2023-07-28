package faang.school.postservice.service;

import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;
}
