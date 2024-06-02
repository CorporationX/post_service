package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static faang.school.postservice.exception.MessagesForCommentsException.NO_POST_IN_DB;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDto getPostById(long id) {
        return postMapper.toDto(postRepository.findById(id).orElseThrow(() -> new DataValidationException(NO_POST_IN_DB.getMessage())));
    }
}
