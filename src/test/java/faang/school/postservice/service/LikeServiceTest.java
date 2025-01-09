package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class LikeServiceTest {
    private final Long id = 1L;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient client;

    @Test
    public void testGetUsersThatLikedThePost() {
        getUsers(likeRepository::findByPostId,
                 likeService::getUsersThatLikedThePost);
    }

    @Test
    public void testGetUsersThatLikedTheComment() {
        getUsers(likeRepository::findByCommentId,
                 likeService::getUsersThatLikedTheComment);
    }

    private void getUsers(Function<Long, List<Like>> function1,
                          Function<Long, List<UserDto>> function2) {

        List<Like> likeList = new ArrayList<>();
        long lengthOfList = 150L;

        for (long i = 1; i <= lengthOfList; i++) {
            Like like = new Like();
            like.setUserId(i);
            likeList.add(like);
        }

        Mockito.when(function1.apply(id)).thenReturn(likeList);
        Mockito.doAnswer(x -> {
                    List<UserDto> userDtos = new ArrayList<>();
                    List<Long> argument = x.getArgument(0);
                    for (long id : argument) {
                        UserDto userDto = new UserDto(id, "", "");
                        userDtos.add(userDto);
                    }
                    return userDtos;
                })
                .when(client).getUsersByIds(anyList());

        List<UserDto> userDtos = function2.apply(id);
        verify(client, times(2)).getUsersByIds(anyList());

        int anyIndexOfElementOfList = 130;
        UserDto userByThatIndexInTheList = userDtos.get(anyIndexOfElementOfList);
        Like likeByThatIndexInTheList = likeList.get(anyIndexOfElementOfList);

        assertEquals(userByThatIndexInTheList.getId(),
                     likeByThatIndexInTheList.getUserId());
    }

}
