package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.model.redis.RedisUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedisUserMapperTest {

    private RedisUserMapper redisUserMapper = new RedisUserMapperImpl();

    private UserDto userDto;
    private RedisUser redisUser;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("user@gmail.com")
                .phone("some phone")
                .city("Kiev")
                .followeeIds(new ArrayList<>(List.of(2L, 3L)))
                .followerIds(new ArrayList<>(List.of(4L, 5L)))
                .build();
        redisUser = RedisUser.builder()
                .userId(1L)
                .username("username")
                .email("user@gmail.com")
                .phone("some phone")
                .city("Kiev")
                .followeeIds(new ArrayList<>(List.of(2L, 3L)))
                .followerIds(new ArrayList<>(List.of(4L, 5L)))
                .build();
    }

    @Test
    void toRedisUserTest() {
        RedisUser result = redisUserMapper.toRedisUser(userDto);
        assertEquals(redisUser, result);
    }

    @Test
    void toUserDtoTest(){
        UserDto result = redisUserMapper.toUserDto(redisUser);
        assertEquals(userDto, result);
    }

    @Test
    void toDtoTest(){
        RedisUserDto expected = RedisUserDto.builder()
                .userId(1L)
                .username("username")
                .email("user@gmail.com")
                .phone("some phone")
                .city("Kiev")
                .followeeIds(new ArrayList<>(List.of(2L, 3L)))
                .followerIds(new ArrayList<>(List.of(4L, 5L)))
                .build();

        RedisUserDto result = redisUserMapper.toDto(redisUser);

        assertEquals(expected, result);
    }
}