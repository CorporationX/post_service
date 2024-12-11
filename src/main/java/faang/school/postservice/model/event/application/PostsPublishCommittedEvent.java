package faang.school.postservice.model.event.application;

import faang.school.postservice.model.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostsPublishCommittedEvent {
    private List<Post> posts;
}
