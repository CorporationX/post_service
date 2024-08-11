package faang.school.postservice.util;

import faang.school.postservice.dto.user.UserDto;
import lombok.experimental.UtilityClass;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static java.util.List.*;

@UtilityClass
public final class TestDataFactory {

    public static final Long ID = 1L;
    public static final Long INVALID_ID = MAX_VALUE;

    public static List<UserDto> getUserDtoList() {
        var userAlex = UserDto.builder()
                .id(1L)
                .username("Alex")
                .email("alex@gmail.com")
                .build();

        var userAnna = UserDto.builder()
                .id(2L)
                .username("Anna")
                .email("anna@gmail.com")
                .build();

        var userOlga = UserDto.builder()
                .id(3L)
                .username("Olga")
                .email("olga@gmail.com")
                .build();

        return of(userOlga, userAnna, userAlex);
    }


}
