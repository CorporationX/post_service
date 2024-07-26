package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public void saveAllHashtags(List<String> hashtagNames) {
        hashtagNames.forEach(hashtagName -> {
            if (!hashtagRepository.existsByName(hashtagName)) {
                Hashtag hashtag = Hashtag.builder()
                        .name(hashtagName)
                        .build();
                hashtagRepository.save(hashtag);
            }
        });
    }

    public List<Hashtag> getHashtagsByName(List<String> hashtagNames) {
        return hashtagRepository.findByNameIn(hashtagNames);
    }
}
