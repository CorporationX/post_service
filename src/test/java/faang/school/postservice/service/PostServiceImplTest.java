package faang.school.postservice.service;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private HashtagService hashtagService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPostsByHashtagOk(){

    }
}
