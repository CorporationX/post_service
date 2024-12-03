package faang.school.postservice.repository.cache.feed;

import faang.school.postservice.dto.cache.feed.FeedCacheDto;

import java.util.Optional;

public interface FeedCacheRepository {

    void save(FeedCacheDto feedCacheDto);

    Optional<FeedCacheDto> findBySubscriberId(Long subscriberId);

    void addPostId(FeedCacheDto feedCacheDto, Long postId);
}

// Все последние посты (200) для всех пользователей ,
// достань всех их подписчиков и повтори все публикации эвентов в кафка через параллель
// несколько потоков

// куда мы сохраняем этот фид? который мы сгенерировали
// если туда же то что с ним делать? И какой от него смысл если мы все это в редис перегоним ?
//