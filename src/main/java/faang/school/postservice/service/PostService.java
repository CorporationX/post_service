package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostDto getPost(Long id) {
        Post post = getById(id);
        return postMapper.toDto(post);
    }

    private Post getById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Поста с указанным id " + id + " не существует"));
    }
}
