package faang.school.postservice.api.media;

import faang.school.postservice.dto.media.MediaDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Temporary stub to simulate media file storage
 */
@Component
public class MockMultipartFileApi implements MultipartFileMediaApi {
    private final ConcurrentMap<String, Pair<MultipartFile, MediaDto>> storage = new ConcurrentHashMap<>();

    public MediaDto save(MultipartFile media) {
        String id = generateRandomString();
        MediaDto mediaDto = createMediaDto(id, media);
        storage.put(id, Pair.of(media, mediaDto));
        return mediaDto;
    }

    @Override
    public List<MediaDto> save(Iterable<MultipartFile> medias) {
        return StreamSupport.stream(medias.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    public MediaDto update(String id, MultipartFile media) {
        if (storage.containsKey(id)) {
            MediaDto mediaDto = createMediaDto(id, media);
            storage.put(id, Pair.of(media, mediaDto));
            return mediaDto;
        }
        throw new RuntimeException("Not found record with id " + id);
    }

    @Override
    public List<MediaDto> update(Map<String, MultipartFile> medias) {
        List<MediaDto> mediaDtos = new ArrayList<>();
        for (Map.Entry<String, MultipartFile> pair : medias.entrySet()) {
            MediaDto updated = update(pair.getKey(), pair.getValue());
            mediaDtos.add(updated);
        }
        return mediaDtos;
    }

    public void delete(String key) {
        storage.remove(key);
    }

    @Override
    public void delete(Set<String> keys) {
        keys.forEach(this::delete);
    }

    @Override
    public Map<String, InputStream> getInputStreams(Set<String> keys) {
        return keys.stream()
                .filter(storage::containsKey)
                .collect(Collectors.toMap(
                        key -> key,
                        key -> {
                            try {
                                return storage.get(key).getFirst().getInputStream();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
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
