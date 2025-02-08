package faang.school.postservice.model.event;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class Event {
    protected long postId;
}
