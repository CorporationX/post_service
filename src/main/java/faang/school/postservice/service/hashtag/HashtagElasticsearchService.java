package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class HashtagElasticsearchService {
    private final HashtagElasticsearchRepository hashtagElasticsearchRepository;
    private final HashtagMapper hashtagMapper;

    public HashtagDto save(HashtagDto hashtagDto) {
        Hashtag hashtag = hashtagMapper.toEntity(hashtagDto);
        Hashtag savedHashtag = hashtagElasticsearchRepository.save(hashtag);
        return hashtagMapper.toDto(savedHashtag);
    }

    public Optional<HashtagDto> findById(Long id) {
        Optional<Hashtag> hashtag = hashtagElasticsearchRepository.findById(id);
        return hashtag.map(hashtagMapper::toDto);
    }

    public List<HashtagDto> findAll() {
        Iterable<Hashtag> hashtags = hashtagElasticsearchRepository.findAll();
        return StreamSupport.stream(hashtags.spliterator(), false)
                .map(hashtagMapper::toDto)
                .toList();
    }

    public void deleteById(Long id) {
        hashtagElasticsearchRepository.deleteById(id);
    }
}