package faang.school.postservice.mapper.comment;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapperHelper {

    private final PostRepository postRepository;

    @Named("postFromId")
    public Post mapPostIdToPost(Long postId) {
        return postId != null ? postRepository.findById(postId).orElse(null) : null;
    }
}