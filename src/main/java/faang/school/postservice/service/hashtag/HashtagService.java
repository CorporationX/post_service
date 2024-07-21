package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public void saveHashtag(String hashtagName) {
        if (!hashtagRepository.existsByName(hashtagName)) {
            Hashtag hashtag = Hashtag.builder()
                    .name(hashtagName)
                    .build();
            hashtagRepository.save(hashtag);
        }
    }

    public Hashtag getHashtag(String hashtagName) {
        return hashtagRepository.findByName(hashtagName);
    }
}
