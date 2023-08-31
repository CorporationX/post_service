package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.messaging.listening.HashtagListener;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;

    @Transactional
    public void saveHashtags(Set<HashtagDto> hashtags){
        for (var hashtagDto : hashtags){
            hashtagRepository.save(hashtagMapper.dtoToEntity(hashtagDto));
        }
    }
}
