package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;

    @Transactional(readOnly = true)
    public ResponsePostDto getById(Long id) {
        return responsePostMapper.toDto(
                postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Post is not found"))
        );
    }

    @Transactional
    public ResponsePostDto softDelete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setDeleted(true);

        return responsePostMapper.toDto(post);
    }
}
