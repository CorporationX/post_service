package faang.school.postservice.service.post.command;

import faang.school.postservice.api.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.post.UpdatablePostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePostResourceCommand {

    private final ResourceRepository resourceRepository;

    private final MultipartFileMediaApi mediaApi;

    private final ResourceMapper resourceMapper;
    private final MediaMapper mediaMapper;

    private final ExecutorService executor;
    private final int taskTimeout;

    public List<Resource> execute(Long postId, List<UpdatableResourceDto> updatableResource) {

        List<UpdatableResourceDto>
                newResources = new LinkedList<>(),
                refreshableResources = new LinkedList<>(),
                deletableResources = new LinkedList<>();

        splitIntoBatchesByUpdatableResourcesState(
                updatableResource,
                newResources,
                refreshableResources,
                deletableResources
        );

        CompletableFuture<List<Resource>> createResFuture = !newResources.isEmpty() ?
                processCreatableResources(postId, newResources)
                :
                CompletableFuture.completedFuture(Collections.emptyList());

        CompletableFuture<List<Resource>> updateResFuture = !refreshableResources.isEmpty() ?
                processUpdatableResources(refreshableResources)
                :
                CompletableFuture.completedFuture(Collections.emptyList());

        CompletableFuture<List<Resource>> deleteResFuture = !deletableResources.isEmpty() ?
                processDeletableResources(deletableResources)
                :
                CompletableFuture.completedFuture(Collections.emptyList());

        try {
            CompletableFuture.allOf(createResFuture, updateResFuture, deleteResFuture).get(
                    taskTimeout,
                    TimeUnit.SECONDS
            );
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            log.error("Task was interrupted by timeout", e);
            throw new RuntimeException(e);
        }

        List<Resource> updatedResources = Stream.concat(
                createResFuture.join().stream(),
                updateResFuture.join().stream()
        ).toList();

        return updatedResources;
    }

    /**
     * @param resources             list of splittable elements.
     * @param outCreatableResources list consisting only of information about created resources.
     * @param outUpdatableResources list consisting only of information about updated resources.
     * @param outDeletableResources list consisting only of information about updated resources.
     * @brief The method splits the total list {@code resources} of updates into 3 batch.
     * depending on the state {@link UpdatablePostDto}
     */
    private void splitIntoBatchesByUpdatableResourcesState(
            @NotNull List<UpdatableResourceDto> resources,
            @NotNull List<UpdatableResourceDto> outCreatableResources,
            @NotNull List<UpdatableResourceDto> outUpdatableResources,
            @NotNull List<UpdatableResourceDto> outDeletableResources) {

        resources.forEach(res -> {
            boolean isCreatableState = res.getResourceId() == null && res.getResource() != null;
            boolean isUpdatableState = res.getResourceId() != null && res.getResource() != null;
            boolean isDeletableState = res.getResourceId() != null && res.getResource() == null;

            if (isCreatableState) {
                outCreatableResources.add(res);
            } else if (isUpdatableState) {
                outUpdatableResources.add(res);
            } else if (isDeletableState) {
                outDeletableResources.add(res);
            } else {
                log.warn("Unknown resource state: {}", res);
                throw new IllegalArgumentException("Unknown resource state: " + res);
            }
        });
    }

    private CompletableFuture<List<Resource>> processCreatableResources(Long postId, List<UpdatableResourceDto> creatable) {
        return provideCompletableFuture(() -> {
            log.info("Start creating resources for post {}.\nResources: {}", postId, creatable);

            List<MultipartFile> media = creatable.stream()
                    .map(UpdatableResourceDto::getResource)
                    .toList();

            log.info("Start saving post media in media storage");
            List<MediaDto> savedMedia = mediaApi.saveAll(media);
            log.info("Post media was saved. Saved: {}", savedMedia);

            List<Resource> resources = savedMedia.stream()
                    .map(m -> {
                        ResourceDto resDto = mediaMapper.toResourceDto(m);
                        return resourceMapper.toEntity(postId, resDto);
                    }).toList();

            log.info("Start saving media info in resource storage");
            List<Resource> savedResources = resourceRepository.saveAll(resources);
            log.info("Post media info was saved. Saved: {}", savedResources);

            return savedResources;
        });
    }

    private CompletableFuture<List<Resource>> processUpdatableResources(List<UpdatableResourceDto> updatable) {
        return provideCompletableFuture(() -> {
            log.info("Start updating post resources.\nResources: {}", updatable);

            Map<Long, MultipartFile> mediaMap = updatable.stream()
                    .collect(Collectors.toMap(
                            UpdatableResourceDto::getResourceId, UpdatableResourceDto::getResource)
                    );

            Map<String, Resource> resourceMap = resourceRepository.findAllById(mediaMap.keySet()).stream()
                    .collect(Collectors.toMap(Resource::getKey, r -> r));

            List<Pair<String, MultipartFile>> updatableMedia = resourceMap.entrySet().stream()
                    .map(e -> {
                        String key = e.getKey();
                        MultipartFile media = mediaMap.get(e.getValue().getId());

                        return Pair.of(key, media);
                    })
                    .toList();

            log.info("Start updating post media. Updatable media: {}", updatableMedia);
            List<MediaDto> savedMedia = mediaApi.updateAll(updatableMedia).orElseThrow();
            log.info("Post media was updated. Updated: {}", savedMedia);

            List<Resource> updatableResource = savedMedia.stream()
                    .map(m -> {
                        Resource res = resourceMap.get(m.getKey());

                        res.setSize(m.getSize());
                        res.setName(m.getName());
                        res.setType(m.getType());

                        return res;
                    })
                    .toList();

            log.info("Start updating media info");
            List<Resource> savedResources = resourceRepository.saveAll(updatableResource);
            log.info("Media info was updated.");

            return savedResources;
        });
    }

    private CompletableFuture<List<Resource>> processDeletableResources(List<UpdatableResourceDto> deletable) {
        return provideCompletableFuture(() -> {
            List<Long> resIds = deletable.stream()
                    .map(UpdatableResourceDto::getResourceId)
                    .toList();

            log.info("Start deleting post resources.\nResources ids: {}", resIds);
            List<Resource> poppedResource = resourceRepository.popAllByIds(resIds);

            List<String> mediaKeys = poppedResource.stream()
                    .map(Resource::getKey)
                    .toList();

            log.info("Start deleting post medias");
            mediaApi.deleteAll(mediaKeys);
            log.info("Post medias was deleted.");

            return poppedResource;
        });
    }

    private <T> CompletableFuture<T> provideCompletableFuture(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var result = supplier.get();
                return result;
            } catch (Exception e) {
                log.error(
                        "Error occurred during CompletableFuture execution in thread {}: {}",
                        Thread.currentThread(), e.getMessage(), e
                );
                throw e;
            }
        }, executor);
    }
}
