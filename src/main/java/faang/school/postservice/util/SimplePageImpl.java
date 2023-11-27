package faang.school.postservice.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class SimplePageImpl<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public SimplePageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") Long totalElements,
                          @JsonProperty("pageable") JsonNode pageable,
                          @JsonProperty("first") boolean first,
                          @JsonProperty("last") boolean last,
                          @JsonProperty("empty") boolean empty,
                          @JsonProperty("totalPages") int totalPages,
                          @JsonProperty("sort") JsonNode sort,
                          @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, 1), 10);
    }

    public SimplePageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public SimplePageImpl(List<T> content) {
        super(content);
    }

    public SimplePageImpl() {
        super(new ArrayList<>());
    }
}
