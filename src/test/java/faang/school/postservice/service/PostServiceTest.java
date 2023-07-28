package faang.school.postservice.service;

import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private ResponsePostMapper responsePostMapper = ResponsePostMapper.INSTANCE;
    @InjectMocks
    private PostService postService;
}