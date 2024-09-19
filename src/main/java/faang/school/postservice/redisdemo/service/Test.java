package faang.school.postservice.redisdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Test {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        List<String> list = List.of("A", "b", "C");
        String str = objectMapper.writeValueAsString(list);
        System.out.println(str);
    }
}
