package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.redis.user.RedisUserCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisUserCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Spy
    private ObjectMapper jackson = new ObjectMapper();

    @InjectMocks
    private RedisUserCacheService redisUserCacheService;

    @Mock
    private RedisConnection redisConnection;
    @Mock
    private RedisStringCommands redisStringCommands;
    @Mock
    private RedisSerializer<String> stringSerializer;

    @Captor
    private ArgumentCaptor<Collection<String>> keysCaptor;
    @Captor
    private ArgumentCaptor<RedisCallback<Object>> redisCallbackCaptor;
    @Captor
    private ArgumentCaptor<byte[]> rawKeyCaptor;
    @Captor
    private ArgumentCaptor<byte[]> rawValueCaptor;
    @Captor
    private ArgumentCaptor<Expiration> expirationCaptor;
    @Captor
    private ArgumentCaptor<RedisStringCommands.SetOption> setOptionCaptor;

    private final long DEFAULT_TTL_MINUTES = 30;
    private final String KEY_PREFIX = "user:";

    // --- bulkGet Tests ---

    @Nested
    @DisplayName("BulkGet Tests")
    class BulkGetTests {
        @Test
        void bulkGet_nullUserIds_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(null, 0, 10);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_emptyUserIds_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(Collections.emptyList(), 0, 10);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_limitZero_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L), 0, 0);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_limitNegative_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L), 0, -1);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_allNullsInUserIds_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(Arrays.asList(null, null), 0, 10);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_offsetOutOfBounds_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L, 2L), 2, 10);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_idsToFetchThisPageEmptyAfterPagination_returnsEmptyList() {
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L, 2L), 1, 0);
            assertTrue(result.isEmpty());
            verify(valueOperations, never()).multiGet(anyCollection());
        }

        @Test
        void bulkGet_success_allFound() throws JsonProcessingException {
            UserDto user1 = new UserDto(1L, "user1", "about1");
            UserDto user2 = new UserDto(2L, "user2", "about2");
            String user1Json = jackson.writeValueAsString(user1);
            String user2Json = jackson.writeValueAsString(user2);

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(keysCaptor.capture())).thenReturn(List.of(user1Json, user2Json));

            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L, 2L), 0, 10);

            assertEquals(2, result.size());
            assertTrue(result.contains(user1));
            assertTrue(result.contains(user2));
            assertEquals(List.of(KEY_PREFIX + "1", KEY_PREFIX + "2"), keysCaptor.getValue());
        }

        @Test
        void bulkGet_success_someFound() throws JsonProcessingException {
            UserDto user1 = new UserDto(1L, "user1", "about1");
            String user1Json = jackson.writeValueAsString(user1);

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(keysCaptor.capture())).thenReturn(new ArrayList<>() {
                {
                    add(user1Json);
                    add(null);
                }
            });
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L, 2L), 0, 10);

            assertEquals(1, result.size());
            assertEquals(user1, result.get(0));
            assertEquals(List.of(KEY_PREFIX + "1", KEY_PREFIX + "2"), keysCaptor.getValue());
        }

        @Test
        void bulkGet_success_paginationApplied() throws JsonProcessingException {
            UserDto user2 = new UserDto(2L, "user2", "about2");
            String user2Json = jackson.writeValueAsString(user2);
            List<Long> userIds = LongStream.rangeClosed(1, 5)
                    .boxed()
                    .toList();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(keysCaptor.capture())).thenReturn(List.of(user2Json));

            List<UserDto> result = redisUserCacheService.bulkGet(userIds, 1, 1);

            assertEquals(1, result.size());
            assertEquals(user2, result.get(0));
            assertEquals(List.of(KEY_PREFIX + "2"), keysCaptor.getValue());
        }


        @Test
        void bulkGet_redisMultiGetReturnsNull_returnsEmptyList() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(anyCollection())).thenReturn(null);
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L), 0, 10);
            assertTrue(result.isEmpty());
        }

        @Test
        void bulkGet_jsonProcessingException_skipsUser() throws JsonProcessingException {
            UserDto user2 = new UserDto(2L, "user2", "about2");
            String user2Json = jackson.writeValueAsString(user2);
            String invalidJson = "{\"id\":1, name:\"user1\"}";

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(anyCollection())).thenReturn(List.of(invalidJson, user2Json));

            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L, 2L), 0, 10);

            assertEquals(1, result.size());
            assertEquals(user2, result.get(0));
        }

        @Test
        void bulkGet_dataAccessException_returnsEmptyList() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(anyCollection())).thenThrow(new DataAccessException("Redis down") {
            });
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L), 0, 10);
            assertTrue(result.isEmpty());
        }

        @Test
        void bulkGet_unexpectedException_returnsEmptyList() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.multiGet(anyCollection())).thenThrow(new RuntimeException("Unexpected error"));
            List<UserDto> result = redisUserCacheService.bulkGet(List.of(1L), 0, 10);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("BulkPut Tests")
    class BulkPutTests {
        private UserDto user1;

        @BeforeEach
        void setUp() {
            user1 = new UserDto(1L, "user1", "about1");
        }

        @Test
        void bulkPut_nullUsers_doesNothing() {
            redisUserCacheService.bulkPut(null);
            verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
        }

        @Test
        void bulkPut_emptyUsers_doesNothing() {
            redisUserCacheService.bulkPut(Collections.emptyList());
            verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
        }

        @Test
        void bulkPut_invalidTtlZero_doesNothing() {
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", 0L);
            redisUserCacheService.bulkPut(List.of(user1));
            verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
        }

        @Test
        void bulkPut_invalidTtlNegative_doesNothing() {
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", -1L);
            redisUserCacheService.bulkPut(List.of(user1));
            verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);
        }

        @Test
        void bulkPut_success() throws JsonProcessingException {
            UserDto user2 = new UserDto(2L, "user2", "about2");
            String user1Json = jackson.writeValueAsString(user1);
            String user2Json = jackson.writeValueAsString(user2);

            when(redisTemplate.executePipelined(any(RedisCallback.class)))
                    .thenAnswer(invocation -> {
                        RedisCallback<Object> callback = invocation.getArgument(0);

                        when(redisConnection.stringCommands()).thenReturn(redisStringCommands);
                        when(redisTemplate.getStringSerializer()).thenReturn(stringSerializer);
                        when(stringSerializer.serialize(anyString()))
                                .thenAnswer(inv -> ((String) inv.getArgument(0)).getBytes());
                        callback.doInRedis(redisConnection);
                        return Collections.emptyList();
                    });
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);
            redisUserCacheService.bulkPut(List.of(user1, user2));

            verify(redisTemplate).executePipelined(redisCallbackCaptor.capture());
            verify(redisStringCommands, times(2))
                    .set(rawKeyCaptor.capture(),
                            rawValueCaptor.capture(),
                            expirationCaptor.capture(),
                            setOptionCaptor.capture());

            List<byte[]> allRawKeys = rawKeyCaptor.getAllValues();
            List<byte[]> allRawValues = rawValueCaptor.getAllValues();
            assertTrue(new String(allRawKeys.get(0))
                    .equals(KEY_PREFIX + "1") || new String(allRawKeys.get(1)).equals(KEY_PREFIX + "1"));
            assertTrue(new String(allRawValues.get(0))
                    .equals(user1Json) || new String(allRawValues.get(1)).equals(user1Json));
            assertTrue(new String(allRawKeys.get(0))
                    .equals(KEY_PREFIX + "2") || new String(allRawKeys.get(1)).equals(KEY_PREFIX + "2"));
            assertTrue(new String(allRawValues.get(0))
                    .equals(user2Json) || new String(allRawValues.get(1)).equals(user2Json));

            assertEquals(DEFAULT_TTL_MINUTES * 60,
                    expirationCaptor.getValue().getExpirationTimeInSeconds());
            assertEquals(RedisStringCommands.SetOption.UPSERT, setOptionCaptor.getValue());
        }

        @Test
        void bulkPut_skipsNullUserInList() {
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);
            when(redisConnection.stringCommands()).thenReturn(redisStringCommands);
            when(redisTemplate.getStringSerializer()).thenReturn(stringSerializer);
            when(stringSerializer.serialize(anyString()))
                    .thenAnswer(inv -> ((String) inv.getArgument(0)).getBytes());

            redisUserCacheService.bulkPut(Arrays.asList(user1, null));

            verify(redisTemplate).executePipelined(redisCallbackCaptor.capture());

            RedisCallback<Object> capturedCallback = redisCallbackCaptor.getValue();
            capturedCallback.doInRedis(redisConnection);
            verify(redisStringCommands, times(1)).set(any(), any(), any(), any());
        }

        @Test
        void bulkPut_skipsUserWithNullIdInList() {
            UserDto userNullId = new UserDto(null, "userNull", "aboutNull");

            when(redisConnection.stringCommands()).thenReturn(redisStringCommands);
            when(redisTemplate.getStringSerializer()).thenReturn(stringSerializer);
            when(stringSerializer.serialize(anyString()))
                    .thenAnswer(inv -> ((String) inv.getArgument(0)).getBytes());
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);

            redisUserCacheService.bulkPut(Arrays.asList(user1, userNullId));

            verify(redisTemplate).executePipelined(redisCallbackCaptor.capture());

            RedisCallback<Object> capturedCallback = redisCallbackCaptor.getValue();
            capturedCallback.doInRedis(redisConnection);
            verify(redisStringCommands, times(1)).set(any(), any(), any(), any()); // Only user1 should be processed
        }

        @Test
        void bulkPut_jsonProcessingExceptionDuringSerialization_skipsProblematicUser()
                throws JsonProcessingException {
            UserDto problematicUser = new UserDto(2L, "problem", "problem");
            JsonProcessingException jsonEx = new JsonProcessingException("Test exception") {};
            String user1Json = jackson.writeValueAsString(user1);
            String expectedUser1Json = jackson.writeValueAsString(user1);
            byte[] expectedUser1Bytes = expectedUser1Json.getBytes();

            ObjectMapper failingMapper = mock(ObjectMapper.class);
            when(failingMapper.writeValueAsString(user1)).thenReturn(user1Json);
            when(failingMapper.writeValueAsString(problematicUser)).thenThrow(jsonEx);
            when(redisConnection.stringCommands()).thenReturn(redisStringCommands);
            when(redisTemplate.getStringSerializer()).thenReturn(stringSerializer);
            when(stringSerializer.serialize(KEY_PREFIX + "1")).thenReturn((KEY_PREFIX + "1").getBytes());
            when(stringSerializer.serialize(user1Json)).thenReturn(user1Json.getBytes());
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);
            ReflectionTestUtils.setField(redisUserCacheService, "jackson", failingMapper);

            redisUserCacheService.bulkPut(List.of(user1, problematicUser));

            verify(redisTemplate).executePipelined(redisCallbackCaptor.capture());

            RedisCallback<Object> capturedCallback = redisCallbackCaptor.getValue();
            capturedCallback.doInRedis(redisConnection);

            verify(redisStringCommands, times(1)).set(
                    eq((KEY_PREFIX + "1").getBytes()),
                    eq(expectedUser1Bytes),
                    any(Expiration.class),
                    eq(RedisStringCommands.SetOption.UPSERT)
            );
            verify(failingMapper).writeValueAsString(user1);
            verify(failingMapper).writeValueAsString(problematicUser);
        }

        @Test
        void bulkPut_dataAccessExceptionOnExecutePipelined() {
            redisUserCacheService.bulkPut(List.of(user1));
            verify(redisStringCommands, never()).set(any(), any(), any(), any());
        }

        @Test
        void bulkPut_unexpectedExceptionDuringPipelinePreparation() throws JsonProcessingException {
            UserDto problematicUser = new UserDto(2L, "problem", "problem");
            String user1Json = jackson.writeValueAsString(user1);
            ObjectMapper failingMapper = mock(ObjectMapper.class);
            String expectedUser1Json = jackson.writeValueAsString(user1);
            byte[] expectedUser1Bytes = expectedUser1Json.getBytes();

            when(failingMapper.writeValueAsString(user1)).thenReturn(user1Json);
            when(failingMapper.writeValueAsString(problematicUser))
                    .thenThrow(new RuntimeException("Unexpected serialization issue"));
            when(redisConnection.stringCommands()).thenReturn(redisStringCommands);
            when(redisTemplate.getStringSerializer()).thenReturn(stringSerializer);
            when(stringSerializer.serialize(KEY_PREFIX + "1")).thenReturn((KEY_PREFIX + "1").getBytes());
            when(stringSerializer.serialize(user1Json))
                    .thenReturn(user1Json.getBytes());
            ReflectionTestUtils.setField(redisUserCacheService, "userCacheTtl", DEFAULT_TTL_MINUTES);
            ReflectionTestUtils.setField(redisUserCacheService, "jackson", failingMapper);

            redisUserCacheService.bulkPut(List.of(user1, problematicUser));

            verify(redisTemplate).executePipelined(redisCallbackCaptor.capture());

            RedisCallback<Object> capturedCallback = redisCallbackCaptor.getValue();
            capturedCallback.doInRedis(redisConnection);

            verify(redisStringCommands, times(1)).set(
                    eq((KEY_PREFIX + "1").getBytes()),
                    eq(expectedUser1Bytes),
                    any(Expiration.class),
                    eq(RedisStringCommands.SetOption.UPSERT));
        }
    }
}
