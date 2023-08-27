package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonObjectMapperTest {
    @InjectMocks
    private JsonObjectMapper jsonMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testToJson_SuccessfulSerialization() throws JsonProcessingException {
        String expectedJson = "{\"name\":\"John\",\"age\":30}";
        User user = new User("John", 30);

        when(objectMapper.writeValueAsString(user)).thenReturn(expectedJson);

        String result = jsonMapper.toJson(user);

        assertNotNull(result);
        assertEquals(expectedJson, result);

        verify(objectMapper).writeValueAsString(user);
    }

    @Test
    void testToJson_SerializationError_ReturnsNull() throws JsonProcessingException {
        User user = new User("John", 30);
        when(objectMapper.writeValueAsString(user)).thenThrow(new JsonProcessingException("Serialization error") {});

        String result = jsonMapper.toJson(user);

        assertNull(result);

        verify(objectMapper).writeValueAsString(user);
    }

    @Test
    void testToJson_JsonProcessingException_ReturnsNull() throws JsonProcessingException {
        User user = new User("John", 30);
        when(objectMapper.writeValueAsString(user)).thenThrow(new JsonProcessingException("Processing error") {});

        String result = jsonMapper.toJson(user);

        assertNull(result);

        verify(objectMapper).writeValueAsString(user);
    }

    private static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}