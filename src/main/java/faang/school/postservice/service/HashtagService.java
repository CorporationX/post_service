package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HashtagService {

    // Извлечь имена хештегов из текста
    List<String> extractHashtagNames(String content);

    // Сохранить хештеги в БД
    void saveHashtags(String content, Long postId);

    // Найти ID постов по имени хештега с пагинацией
    Page<PostDto> findPostsByHashtagName(String name, Pageable pageable);
}