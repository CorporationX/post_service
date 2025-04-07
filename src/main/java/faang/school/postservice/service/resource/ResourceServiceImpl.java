package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.dto.resource.S3UploadDto;
import faang.school.postservice.exception.FileValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.service.thumbnails.ImageResizeImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ImageResizeImpl imageResizeImpl;

    @Override
    public ResponseEntity<List<ResourceResponseDto>> uploadImageResource(Long postId, MultipartFile file) {
        String fileContentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        validateFileName(fileName);

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!ThumbnailatorUtils.isSupportedOutputFormat(fileExtension)) {
            throw new FileValidationException("Unsupported file extension: " + fileExtension);
        }

        List<S3UploadDto> s3UploadDtos = new ArrayList<>();

        byte[] bytes = getBytes(file);
        s3UploadDtos.add(new S3UploadDto(postId, file.getSize(), fileName, fileContentType, bytes));

        imageResizeImpl.getResizedImages(bytes)
                .forEach((resizedSize, resizedImageBytes) ->
                        s3UploadDtos.add(
                                new S3UploadDto(
                                        postId,
                                        (long) resizedImageBytes.length,
                                        "resized_" + resizedSize + "_" + fileName,
                                        fileContentType,
                                        resizedImageBytes))
                );

        log.debug("Total images for upload: {}", s3UploadDtos.size());

        return new ResponseEntity<>(
                s3UploadDtos.stream()
                        .map(s3Service::uploadResource)
                        .map(resourceRepository::save)
                        .map(resourceMapper::toResourceDto)
                        .toList(),
                HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResourceResponseDto> uploadResource(Long postId, MultipartFile file) {
        String fileContentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        validateFileName(fileName);

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (ThumbnailatorUtils.isSupportedOutputFormat(fileExtension)) {
            throw new FileValidationException("Use another endpoint for upload images: " + fileExtension);
        }

        byte[] bytes = getBytes(file);
        Resource uploadedResource = upload(postId, file.getSize(), fileName, fileContentType, bytes);
        log.debug("Resource with name: {} uploaded to S3 successfully", fileName);
        return new ResponseEntity<>(
                resourceMapper.toResourceDto(resourceRepository.save(uploadedResource)),
                HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<byte[]> downloadResource(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        try (InputStream inputStream = s3Service.downloadResource(resource.getKey())) {
            log.debug("Resource: {} downloaded successfully", resourceId);
            return new ResponseEntity<>(
                    inputStream.readAllBytes(),
                    createHttpHeadersForType(resource.getType()),
                    HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error while downloading resource: {}", resourceId);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteResource(Long resourceId) {
        s3Service.deleteResource(getResourceKey(resourceId));
        resourceRepository.deleteResourceById(resourceId);
        log.debug("Resource: {} deleted successfully", resourceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private Resource upload(Long postId, long fileSize, String fileName, String contentType, byte[] fileBytes) {
        Resource uploadedResource = s3Service.uploadResource(
                new S3UploadDto(postId, fileSize, fileName, contentType, fileBytes));
        log.debug("Resource with name: {} uploaded successfully", fileName);
        return uploadedResource;
    }

    private String getResourceKey(Long resourceId) {
        return resourceRepository.findResourceKeyById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find resource with id: " + resourceId));
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id: " + resourceId + " not found"));
    }

    private HttpHeaders createHttpHeadersForType(String type) {
        HttpHeaders headers = new HttpHeaders();
        if (type != null) {
            try {
                headers.setContentType(MediaType.parseMediaType(type));
            } catch (InvalidMediaTypeException e) {
                log.error("Invalid media type: {} message: {}", type, e.getMessage());
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        return headers;
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank() || !fileName.contains(".")) {
            throw new FileValidationException("Something went wrong with file: " + fileName);
        }
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error("Error while getting bytes from file: {}", file.getOriginalFilename());
            throw new RuntimeException(e);
        }
    }
}
