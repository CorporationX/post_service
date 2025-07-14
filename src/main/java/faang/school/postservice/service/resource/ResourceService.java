package faang.school.postservice.service.resource;

import faang.school.postservice.config.resource.ResourceProperties;
import faang.school.postservice.dto.resource.StoredFile;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.UploadFileException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.resource.upload.UploadStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final List<UploadStrategy> uploadStrategies;
    private final ResourceProperties resourceProperties;

    public List<Resource> uploadResources(List<MultipartFile> files, int existingResourceCount) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        int total = existingResourceCount + files.size();
        if (total > resourceProperties.getMaxFilesPerPost()) {
            throw new DataValidationException("Exceeded maximum number of files per post: " + resourceProperties.getMaxFilesPerPost());
        }

        Map<UploadStrategy, List<MultipartFile>> grouped = files.stream()
                .collect(Collectors.groupingBy(this::findStrategy));

        List<CompletableFuture<List<StoredFile>>> futures = grouped.entrySet().stream()
                .map(entry -> entry.getKey().upload(entry.getValue()))
                .toList();

        CompletableFuture<Void> allUploads = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allUploads.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Upload failed", e);
            throw new UploadFileException("Failed to upload files");
        }

        return futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        log.error("Failed to retrieve uploaded file info", e);
                        throw new UploadFileException("Failed to retrieve uploaded file info");
                    }
                })
                .map(file -> Resource.builder()
                        .key(file.key())
                        .name(file.name())
                        .size(file.size())
                        .type(file.type())
                        .build())
                .toList();
    }

    public void deleteResources(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return;
        }

        for (Resource resource : resources) {
            Optional<UploadStrategy> strategyOpt = uploadStrategies.stream()
                    .filter(strategy -> strategy.getType().equalsIgnoreCase(resource.getType()))
                    .findFirst();

            if (strategyOpt.isPresent()) {
                strategyOpt.get().delete(resource.getKey());
            } else {
                log.warn("No strategy found to delete resource type: {}", resource.getType());
            }
        }
    }

    private UploadStrategy findStrategy(MultipartFile file) {
        return uploadStrategies.stream()
                .filter(strategy -> strategy.supports(file.getContentType()))
                .findFirst()
                .orElseThrow(() -> new DataValidationException("Unsupported file type: " + file.getContentType()));
    }
}

