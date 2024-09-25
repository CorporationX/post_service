package faang.school.postservice;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceAppTests {
    @InjectMocks
    private LikeService service;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private LikeMapperImpl mapper;


    @Test
    @DisplayName("Add like to post: check user exist")
    public void testAddToPostUserExist() {
        Like templike = new Like();
        templike.setUserId(2L);
        Mockito.when(userServiceClient.getUser(templike.getUserId()))
                .thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> service.addToPost(1L, templike));
    }

    @Test
    @DisplayName("Add like to post: check post exist")
    public void testAddToPostPostExist() {
        Like templike = new Like();
        templike.setUserId(2L);

        UserDto userDto = new UserDto(1L, "Alex", "alex@mail.com" );
        Mockito.when(userServiceClient.getUser(1))
                .thenReturn(userDto);

        Mockito.when(postRepository.findById(1L)).thenThrow();

        Assert.assertThrows(RuntimeException.class, () -> service.addToPost(1L, templike));
    }

}
