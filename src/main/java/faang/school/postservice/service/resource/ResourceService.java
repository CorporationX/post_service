package faang.school.postservice.service.resource;

import faang.school.postservice.api.media.MultipartFileMediaApi;
import faang.school.postservice.dto.media.MediaDto;
import faang.school.postservice.dto.resource.PostResourceDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.post.MediaMapper;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.List.of;

@Service
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final MultipartFileMediaApi mediaApi;
    private final ResourceMapper resourceMapper;
    private final MediaMapper mediaMapper;

    public ResourceService(
            ResourceRepository resourceRepository,
            @Qualifier("s3MediaApi") MultipartFileMediaApi mediaApi,
            ResourceMapper resourceMapper,
            MediaMapper mediaMapper
    ) {
        this.resourceRepository = resourceRepository;
        this.mediaApi = mediaApi;
        this.resourceMapper = resourceMapper;
        this.mediaMapper = mediaMapper;
    }


    public List<ResourceDto> createResources(long postId, List<MultipartFile> creatableRes) {
        log.info("Start creating resources for post {}.\nResources: {}", postId, creatableRes);

        log.info("Start saving post media in media storage");
        List<MediaDto> savedMedia = mediaApi.save(creatableRes);
        log.info("Post media was saved. Saved: {}", savedMedia);

        List<Resource> resources = savedMedia.stream()
                .map(m -> {
                    ResourceDto resDto = mediaMapper.toResourceDto(m);
                    return resourceMapper.toEntity(postId, resDto);
                })
                .toList();

        log.info("Start saving media info in resource storage");
        List<Resource> savedResources = resourceRepository.saveAll(resources);
        log.info("Post media info was saved. Saved: {}", savedResources);

        List<ResourceDto> resourceDtos = savedResources.stream()
                .map(resourceMapper::toDto)
                .toList();

        return resourceDtos;
    }

    public List<ResourceDto> updateResources(Map<Long, MultipartFile> updatableRes) {
        log.info("Start updating post resources.\nResources: {}", updatableRes);

        Map<String, Resource> resourceMap = resourceRepository.findAllById(updatableRes.keySet()).stream()
                .collect(Collectors.toMap(Resource::getKey, r -> r));

        Map<String, MultipartFile> updatableMedia = resourceMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> updatableRes.get(e.getValue().getId())
                ));

        log.info("Start updating post media. Updatable media: {}", updatableMedia);
        List<MediaDto> savedMedia = mediaApi.update(updatableMedia);
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

        List<ResourceDto> resourceDtos = savedResources.stream()
                .map(resourceMapper::toDto)
                .toList();

        return resourceDtos;
    }

    public void deleteResources(Set<Long> resourceIds) {

        log.info("Start deleting post resources.\nResources ids: {}", resourceIds);
        List<Resource> poppedResource = resourceRepository.popAllByIds(resourceIds);

        Set<String> mediaKeys = poppedResource.stream()
                .map(Resource::getKey)
                .collect(Collectors.toSet());

        log.info("Start deleting post medias");
        mediaApi.delete(mediaKeys);
        log.info("Post medias was deleted.");
    }

    public List<PostResourceDto> getPostResources(long postId) {

        List<Resource> resources = resourceRepository.findAllByPostId(postId);

        Set<String> resKeys = resources.stream()
                .map(Resource::getKey)
                .collect(Collectors.toSet());

        Map<String, InputStream> medias = mediaApi.getInputStreams(resKeys);

        List<PostResourceDto> postResources = resources.stream()
                .map(res -> {
                    InputStream data = medias.get(res.getKey());
                    return resourceMapper.toPostResourceDto(res, data);
                })
                .toList();

        return postResources;
    }
}
