package faang.school.postservice.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataDoesNotExistException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostMapperImpl postMapper;
    private PostRepository postRepository;
    @InjectMocks
    private PostService service;

    @Test
    public void testCreateDraftPost(){
        Post post = new Post();
        post.setId(1L);
        post.setContent("Test");
        post.setAuthorId(1L);
        when(postMapper.toEntity(any())).thenReturn(post);

    }

//    @Test
//    public void testUserDoesNotExistCreatingPost(){
//        UserServiceClient userServiceClient =
//        PostDto dto = new PostDto();
//        dto.setAuthorId(1L);
//        Post post = new Post();
//        post.setAuthorId(1L);
//        UserDto userDto = new UserDto();
//        when(postMapper.toEntity(any())).thenReturn(post);
//        when(userServiceClient.getUser(post.getPostId())).thenReturn()
//        assertThrows(DataDoesNotExistException.class, () -> service.createDraftPost(dto));
//    }
}
