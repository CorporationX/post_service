package faang.school.postservice.model.event.application;

import faang.school.postservice.model.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCommittedEvent {
    private CommentDto commentDto;
}
