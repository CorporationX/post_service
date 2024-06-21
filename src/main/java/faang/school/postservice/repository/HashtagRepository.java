package faang.school.postservice.repository;

import faang.school.postservice.jpa.HashtagJpaRepository;
import faang.school.postservice.model.Hashtag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashtagRepository {

    private final HashtagJpaRepository hashtagJpaRepository;

    public Hashtag findByName(String name) {
        return hashtagJpaRepository.findByName(name).orElseThrow(() ->
                new EntityNotFoundException(String.format("Hashtag with name: %s not found", name)));
    }
}
