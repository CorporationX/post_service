package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public List<PostDto> getPostsByProjectId(long projectId) {
        log.info("Запрос постов для проекта с ID: {}", projectId);
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
        log.info("Найдено {} постов для проекта с ID: {}", posts.size(), projectId);
        return posts.stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> getPostsByAuthorId(long userId) {
        log.info("Запрос постов для пользователя с ID: {}", userId);
        List<Post> posts = postRepository.findByAuthorIdWithLikes(userId);
        log.info("Найдено {} постов для пользователя с ID: {}", posts.size(), userId);
        return posts.stream()
                .map(postMapper::toDto)
                .toList();
    }
}