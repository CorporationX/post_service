package faang.school.postservice.service;

import faang.school.postservice.model.dto.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.entity.Hashtag;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.service.impl.HashtagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

class HashtagServiceTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private HashtagMapper hashtagMapper;

    @Mock
    private PostService postService;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    private Set<HashtagDto> hashtagDtos;
    private Post post;
    private Hashtag hashtag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        HashtagDto hashtagDto = HashtagDto.builder()
                .id(1L)
                .postId(100L)
                .content("#example")
                .build();

        hashtagDtos = new HashSet<>();
        hashtagDtos.add(hashtagDto);

        post = new Post();
        post.setId(1L);
        post.setHashtags(new HashSet<>());

        hashtag = new Hashtag();
        hashtag.setId(1L);
    }
}