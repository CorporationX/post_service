package faang.school.postservice.api.media;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.api.processor.MultipartFileProcessor;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.exception.BaseRuntimeException;
import faang.school.postservice.exception.resource.GroupResProcessingException;
import faang.school.postservice.exception.resource.ResourceProcessingException;
import faang.school.postservice.repository.S3ObjectRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class S3MediaApi extends S3ObjectRepository implements MultipartFileMediaApi {

    private final List<MultipartFileProcessor> mediaProcessors;

    public S3MediaApi(
            AmazonS3 client,
            @Qualifier("post-resource-bucket") String bucketName,
            List<MultipartFileProcessor> mediaProcessors
    ) {
        super(client, bucketName);
        this.mediaProcessors = mediaProcessors != null ?
                mediaProcessors : Collections.emptyList();
    }

    @Override
    public List<MediaDto> save(Iterable<MultipartFile> medias) {

        Iterable<MultipartFile> processed = processMediaGroupIfNecessary(
                medias,
                (var m) -> m,
                (var item, var file) -> file
        );

        return StreamSupport.stream(processed.spliterator(), false)
                .map(m -> putMultipartFilleAndGetMediaDto(
                                m,
                                this::generateKey
                        )
                )
                .toList();
    }

    private <T> Iterable<T> processMediaGroupIfNecessary(
            Iterable<T> mediaGroup,
            Function<T, MultipartFile> mediaProvider,
            BiFunction<T, MultipartFile, T> composer
    ) {
        ConcurrentLinkedQueue<BaseRuntimeException> exp = new ConcurrentLinkedQueue<>();
        List<T> processed = StreamSupport.stream(mediaGroup.spliterator(), true)
                .map(m -> {
                    try {
                        var file = mediaProvider.apply(m);
                        var proc = processMediaIfNecessary(file);
                        return composer.apply(m, proc);
                    } catch (ResourceProcessingException e) {
                        exp.add(e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!exp.isEmpty()) {
            throw new GroupResProcessingException(
                    exp
            );
        }

        return processed;
    }

    private MultipartFile processMediaIfNecessary(MultipartFile media) {

        AtomicReference<MultipartFile> processable = new AtomicReference<>(media);

        mediaProcessors.stream()
                .filter(proc -> proc.canBeProcessed(media.getContentType(), media))
                .forEach(proc -> processable.set(
                                proc.process(media)
                        )
                );

        return processable.get();
    }

    private MediaDto putMultipartFilleAndGetMediaDto(MultipartFile file, Supplier<String> keyProvider) {
        InputStream stream;
        try {
            stream = file.getInputStream();
        } catch (IndexOutOfBoundsException | IOException e) {
            throw new RuntimeException(e);
        }

        String key = keyProvider.get();
        putObject(key, stream, file.getSize(), file.getContentType());

        return new MediaDto(
                key,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<MediaDto> update(Map<String, MultipartFile> medias) {

        Iterable<Map.Entry<String, MultipartFile>> processed = processMediaGroupIfNecessary(
                medias.entrySet(),
                Map.Entry::getValue,
                (var entry, var file) -> Map.entry(entry.getKey(), file)
        );

        List<MediaDto> updated = StreamSupport.stream(processed.spliterator(), false)
                .map(e -> putMultipartFilleAndGetMediaDto(
                        e.getValue(),
                        e::getKey
                ))
                .toList();

        return updated;
    }

    @Override
    public void delete(Set<String> keys) {
        keys.forEach(this::deleteObject);
    }

    @Override
    public Map<String, InputStream> getInputStreams(Set<String> keys) {
        return keys.stream().collect(Collectors.toMap(
                key -> key,
                this::getObject
        ));
    }
}
