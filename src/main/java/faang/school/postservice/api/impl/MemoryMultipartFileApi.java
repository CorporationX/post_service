package faang.school.postservice.api.impl;

import faang.school.postservice.api.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Temporary stub to simulate media file storage
 */
@Component
public class MemoryMultipartFileApi implements MultipartFileMediaApi {
    private final ConcurrentMap<String, Pair<MultipartFile, MediaDto>> storage = new ConcurrentHashMap<>();

    @Override
    public MediaDto save(MultipartFile media) {
        String id = generateRandomString();
        MediaDto mediaDto = createMediaDto(id, media);
        storage.put(id, Pair.of(media, mediaDto));
        return mediaDto;
    }

    @Override
    public List<MediaDto> saveAll(Iterable<MultipartFile> medias) {
//        List<MediaDto> mediaDtos = new ArrayList<>();
//        for (MultipartFile media : medias) {
//            mediaDtos.add(save(media));
//        }
        return StreamSupport.stream(medias.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MediaDto> update(String id, MultipartFile media) {
        if (storage.containsKey(id)) {
            MediaDto mediaDto = createMediaDto(id, media);
            storage.put(id, Pair.of(media, mediaDto));
            return Optional.of(mediaDto);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<MediaDto>> updateAll(Iterable<Pair<String, MultipartFile>> medias) {
        List<MediaDto> mediaDtos = new ArrayList<>();
        for (Pair<String, MultipartFile> pair : medias) {
            Optional<MediaDto> updated = update(pair.getFirst(), pair.getSecond());
            if (updated.isPresent()) {
                mediaDtos.add(updated.get());
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(mediaDtos);
    }

    @Override
    public void delete(String key) {
        storage.remove(key);
    }

    @Override
    public void deleteAll(Iterable<String> keys) {
        keys.forEach(this::delete);
    }

    private String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private MediaDto createMediaDto(String id, MultipartFile media) {
        return MediaDto.builder()
                .key(id)
                .size(media.getSize())
                .type(media.getContentType())
                .name(media.getOriginalFilename())
                .build();
    }
}
