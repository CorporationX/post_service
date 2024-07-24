package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static java.lang.Long.*;
import static java.util.Collections.*;
import static java.util.List.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    UserServiceClient userServiceClient;

    private static final Long ID = 1L;
    private static final Long INVALID_ID = MAX_VALUE;

    @BeforeEach
    void setUp() {
        likeService.setBatchSize(100);
    }
    @Test
    void givenPostIdWhenFindUsersByPostIdThenReturnUsers() {
        // given - precondition
        List<Like> likeList = of(new Like(), new Like());
        List<UserDto> userDtoList = TestDataFactory.getUserDtoList();

        when(likeRepository.findByPostId(ID))
                .thenReturn(likeList);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(userDtoList);

        // when - action
        var actualResult = likeService.findUsersByPostId(ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveFieldByFieldElementComparator()
                .containsAnyElementsOf(userDtoList);
    }

    @Test
    void givenInvalidPostIdWhenFindUsersByPostIdThenThrowException() {
        // given - precondition
        when(likeRepository.findByPostId(anyLong()))
                .thenReturn(emptyList());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> likeService.findUsersByPostId(INVALID_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No users found for ID " + INVALID_ID);
    }

    @Test
    void givenCommentIdWhenFindUsersByPostIdThenReturnUsers() {
        // given - precondition
        List<Like> likeList = of(new Like(), new Like());
        List<UserDto> userDtoList = TestDataFactory.getUserDtoList();

        when(likeRepository.findByCommentId(anyLong()))
                .thenReturn(likeList);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(userDtoList);

        // when - action
        var actualResult = likeService.findUsersByCommentId(ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).usingRecursiveFieldByFieldElementComparator()
                .containsAnyElementsOf(userDtoList);
    }
    @Test
    void givenInvalidCommentIdWhenFindUsersByPostIdThenThrowException() {
        // given - precondition
        when(likeRepository.findByCommentId(anyLong()))
                .thenReturn(emptyList());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> likeService.findUsersByCommentId(INVALID_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No users found for ID " + INVALID_ID);
    }
}