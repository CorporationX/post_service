package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;

    @Transactional
    public ResponsePostDto update(UpdatePostDto dto) {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }
}
