package faang.school.postservice.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.util.exception.NotAllowedException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.util.validator.AccessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessValidatorTest {

    @Spy
    ObjectMapper objectMapper;

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    AccessValidator accessValidator;

    Album album;

    @BeforeEach
    void setUp() {
        album = Album.builder().id(1).build();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void testValidateUpdateAccess_Success(int userId) {
        album.setAuthorId(userId);
        accessValidator.validateUpdateAccess(album, userId);
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    public void testValidateUpdateAccess_Failure(int userId) {
        album.setAuthorId(userId + 1);
        assertThrows(
                NotAllowedException.class,
                () -> accessValidator.validateUpdateAccess(album, userId)
        );
    }

    @Test
    public void testValidateAccess_EveryoneVisibility() throws JsonProcessingException {
        album.setVisibility(Visibility.EVERYONE);
        accessValidator.validateAccess(album, 123L);

        verifyNoInteractions(userServiceClient);
    }

    @Test
    public void testValidateAccess_ONLY_ME() {
        album.setVisibility(Visibility.ONLY_ME);
        album.setAuthorId(1);
        album.setAllowedUsersIds("[2]");

        assertDoesNotThrow(
                () -> accessValidator.validateAccess(album, 1)
        );
        assertThrows(
                NotAllowedException.class,
                () -> accessValidator.validateAccess(album, album.getAuthorId() + 2)
        );
    }

    @Test
    public void testValidateAccess_SPECIFIC_USERS() {
        album.setVisibility(Visibility.SPECIFIC_USERS);
        album.setAuthorId(2);
        album.setAllowedUsersIds("[1,2]");

        List<UserDto> mockUserList = List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build()
        );
        when(userServiceClient.getUsersByIds(Mockito.anyList()))
                .thenReturn(mockUserList);

        assertDoesNotThrow(
                () -> accessValidator.validateAccess(album, 2L)
        );
        assertThrows(
                NotAllowedException.class,
                () -> accessValidator.validateAccess(album, 4)
        );
    }

    @Test
    public void testValidateAccess_FOLLOWERS() throws IOException {
        album.setVisibility(Visibility.FOLLOWERS);
        album.setAuthorId(1);
        album.setAllowedUsersIds("[1]");

        when(userServiceClient.getFollowing(Mockito.anyLong())).thenReturn(List.of(UserDto.builder().id(2L).build()));

        assertDoesNotThrow(
                () -> accessValidator.validateAccess(album, 2)
        );
        assertThrows(
                NotAllowedException.class,
                () -> accessValidator.validateAccess(album, 3)
        );
    }


    private static Stream<Integer> provideArguments() {
        return Stream.of(1, 2, 3);
    }
}
