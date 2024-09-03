package faang.school.postservice.service.post.command;

import faang.school.postservice.api.media.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.post.UpdatablePostDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class UpdatePostResourceCommand {

    private final ResourceService resourceService;
    private final ExecutorService executor;
    private final int taskTimeout;

    public UpdatePostResourceCommand(
            ResourceService resourceService,
            @Qualifier("post-service-thread-pool") ExecutorService executor,
            int taskTimeout) {
        this.resourceService = resourceService;
        this.executor = executor;
        this.taskTimeout = taskTimeout;
    }

    public List<ResourceDto> execute(Long postId, List<UpdatableResourceDto> updatableResource) {

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

        CompletableFuture<List<ResourceDto>> createResFuture = !newResources.isEmpty() ?
                processCreatableResources(postId, newResources)
                :
                CompletableFuture.completedFuture(Collections.emptyList());

        CompletableFuture<List<ResourceDto>> updateResFuture = !refreshableResources.isEmpty() ?
                processUpdatableResources(refreshableResources)
                :
                CompletableFuture.completedFuture(Collections.emptyList());

        CompletableFuture<List<ResourceDto>> deleteResFuture = !deletableResources.isEmpty() ?
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

        List<ResourceDto> updatedResources = Stream.concat(
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

        resources.forEach(r -> {

            if (r.isCreatableState()) {
                outCreatableResources.add(r);
            } else if (r.isUpdatableState()) {
                outUpdatableResources.add(r);
            } else if (r.isDeletableState()) {
                outDeletableResources.add(r);
            } else {
                log.warn("Unknown resource state: {}", r);
                throw new IllegalArgumentException("Unknown resource state: " + r);
            }
        });
    }

    private CompletableFuture<List<ResourceDto>> processCreatableResources(Long postId, List<UpdatableResourceDto> creatable) {
        return provideCompletableFuture(() -> {
            List<MultipartFile> files = creatable.stream()
                    .map(
                            UpdatableResourceDto::getResource
                    )
                    .toList();

            var created = resourceService.createResources(postId, files);

            return created;
        });
    }

    private CompletableFuture<List<ResourceDto>> processUpdatableResources(List<UpdatableResourceDto> updatable) {
        return provideCompletableFuture(() -> {
            Map<Long, MultipartFile> res = updatable.stream().collect(Collectors.toMap(
                    UpdatableResourceDto::getResourceId,
                    UpdatableResourceDto::getResource
            ));

            var updated = resourceService.updateResources(res);

            return updated;
        });
    }

    private CompletableFuture<List<ResourceDto>> processDeletableResources(List<UpdatableResourceDto> deletable) {
        return provideCompletableFuture(() -> {
            Set<Long> deletableIds = deletable.stream()
                    .map(UpdatableResourceDto::getResourceId)
                    .collect(Collectors.toSet());

            resourceService.deleteResources(deletableIds);

            return Collections.emptyList();
        });
    }

    private <T> CompletableFuture<T> provideCompletableFuture(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
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
