package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
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
    public ResponsePostDto publish(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        if (post.isPublished()){
            throw new IllegalArgumentException("Can't publish already published post");
        }
        if (post.isDeleted()){
            throw new IllegalArgumentException("Post has been deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }
}
