package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Сервис для управления хештегами в постах.
 * Предоставляет функциональность для извлечения, сохранения и поиска постов по хештегам.
 */
public interface HashtagService {

    /**
     * Извлекает имена хештегов из указанного контента.
     * Хештеги определяются символом '#', за которым следуют буквенно-цифровые символы.
     *
     * @param content текстовый контент для извлечения хештегов
     * @return список имён хештегов, найденных в контенте (например, ["#java", "#spring"])
     */
    List<String> extractHashtagNames(String content);

    /**
     * Сохраняет все хештеги, найденные в контенте, в базу данных.
     * Связывает каждый хештег с указанным ID поста.
     *
     * @param content текстовый контент, содержащий хештеги
     * @param postId  ID поста, с которым связываются хештеги
     */
    void saveHashtags(String content, Long postId);

    /**
     * Находит все посты, содержащие указанный хештег, с поддержкой пагинации.
     * Результаты сортируются по дате создания в порядке убывания (новые первыми).
     *
     * @param name имя хештега для поиска (например, "#java")
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы (количество постов на странице)
     * @return постраничный список постов, содержащих указанный хештег
     */
    Page<PostDto> findPostsByHashtagName(String name, int page, int size);
}