package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagReadDto;
import faang.school.postservice.dto.hashtag.HashtagUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagMapper hashtagMapper;
    private final HashtagRepository hashtagRepository;

    public HashtagReadDto create(HashtagCreateDto createDto) {
        validateHashtagNameExists(createDto.name());
        Hashtag newHashtag = hashtagMapper.toEntity(createDto);
        newHashtag = hashtagRepository.save(newHashtag);
        return hashtagMapper.toDto(newHashtag);
    }

    public HashtagReadDto update(HashtagUpdateDto updateDto) {
        Hashtag hashtag = getHashtagById(updateDto.id());
        validateHashtagNameExists(updateDto.name());
        hashtagMapper.updateEntityFromDto(updateDto, hashtag);

        hashtag = hashtagRepository.save(hashtag);
        return hashtagMapper.toDto(hashtag);
    }

    public HashtagReadDto getHashtag(long hashtagId) {
        return hashtagMapper.toDto(getHashtagById(hashtagId));
    }

    public List<HashtagReadDto> getHashtagsByPostId(long postId) {
        List<Hashtag> hashtags = hashtagRepository.findAllByPostId(postId);

        return hashtags.stream()
                .map(hashtagMapper::toDto)
                .toList();
    }

    public void remove(long hashtagId) {
        hashtagRepository.deleteById(hashtagId);
    }

    public Hashtag getHashtagById(long hashtagId) {
        return hashtagRepository.findById(hashtagId)
                .orElseThrow(() -> new EntityNotFoundException("Хэштег с ID " + hashtagId + " не найден"));
    }

    public boolean isHashtagExist(long hashtagId) {
        return hashtagRepository.existsById(hashtagId);
    }

    public void validateHashtagNameExists(String name) {
        if (hashtagRepository.findByName(name).isPresent()) {
            throw new DataValidationException("Хэштег с названием " + name + " уже существует");
        }
    }

}