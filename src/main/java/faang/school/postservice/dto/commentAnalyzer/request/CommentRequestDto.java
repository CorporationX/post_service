package faang.school.postservice.dto.commentAnalyzer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    private CommentDto comment;
    private Map<String, Object> requestedAttributes;
    private boolean doNotStore;

    public CommentRequestDto(String text) {
        this.comment = new CommentDto(text);
        this.requestedAttributes = new HashMap<>();
        for (RequestedAttributeType type : EnumSet.allOf(RequestedAttributeType.class)) {
            this.requestedAttributes.put(type.name(), new HashMap<>());
        }
        this.doNotStore = true;
    }
}
