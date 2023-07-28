package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
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
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostService postService;

//    @Test
//    void createTest() {
//        CreatePostDto blankContent = CreatePostDto.builder().authorId(1L).content("").build();
//        CreatePostDto correct = CreatePostDto.builder().authorId(1L).content("Content").build();
//
//        List<CreatePostDto> createPostDtoList = List.of(bothNotNull, blankContent, correct);
//
//        List<>
//        createPostDtoList.forEach(postDto -> );
//    }

    @Test
    void createBothNotNull(){}
    CreatePostDto bothNotNull = CreatePostDto.builder().authorId(1L).projectId(1L).build();

    IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> postService.createDraft(bothNotNull));

    assertEquals()
}