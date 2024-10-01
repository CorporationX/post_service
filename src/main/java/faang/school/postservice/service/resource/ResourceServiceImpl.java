package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.FileUploadException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final static int IMAGES_IN_POST_LIMIT = 10;
    private final static long MAX_FILE_SIZE = 5_242_880L;
    private final static int MAX_SQUARE_PHOTO_RESOLUTION = 1080;
    private final static int MAX_PHOTO_WIDTH = 1080;
    private final static int MAX_PHOTO_HEIGHT = 566;

    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Service s3Service;

    @Override
    @Transactional
    public ResourceDto addResource(Long post_id, MultipartFile file) {
        Post post = postRepository.findById(post_id).get();

        int postImageCount = post.getResources().size();
        checkImageCount(postImageCount);
        checkFileSize(file);
        MultipartFile correctedFile = correctImageResolution(file);

        String folder = post_id.toString();

        Resource resource = s3Service.uploadFile(correctedFile, folder);
        resource.setPost(post);
        post.getResources().add(resource);

        resourceRepository.save(resource);
        postRepository.save(post);

        return resourceMapper.toDto(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long resource_id) {
        Resource resource = resourceRepository.getById(resource_id);
        Post post = resource.getPost();

        post.getResources().remove(resource);
        s3Service.deleteFile(resource.getKey());
        postRepository.save(post);
        resourceRepository.delete(resource);
    }

    @Override
    public InputStream downloadResource(Long resource_id) {
        String key = resourceRepository.getById(resource_id).getKey();

        return s3Service.downloadFile(key);
    }

    private void checkImageCount(int postImageCount) {
        if (!(postImageCount + 1 <= IMAGES_IN_POST_LIMIT)) {
            throw new FileUploadException("only " + IMAGES_IN_POST_LIMIT + " images allowed");
        }
    }

    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("only " + MAX_FILE_SIZE + " file size allowed");
        }
    }

    private MultipartFile correctImageResolution(MultipartFile file) {
        BufferedImage bufferedImage;
        BufferedImage resizedImage;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (height == width & height > MAX_SQUARE_PHOTO_RESOLUTION) {
            resizedImage = resizeImage(bufferedImage, MAX_SQUARE_PHOTO_RESOLUTION, MAX_SQUARE_PHOTO_RESOLUTION);
        } else if (height != width & height >= MAX_PHOTO_HEIGHT || width >= MAX_PHOTO_WIDTH) {
            resizedImage = resizeImage(bufferedImage, MAX_PHOTO_WIDTH, MAX_PHOTO_HEIGHT);
        } else {
            resizedImage = bufferedImage;
        }
        return convertBufferedImageToMultipartFile(resizedImage, file.getOriginalFilename(), file.getContentType());
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private MultipartFile convertBufferedImageToMultipartFile(BufferedImage image, String originalFilename, String contentType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            log.error("Failed to convert image", e);
            throw new RuntimeException("Failed to convert image", e);
        }

        byte[] imageBytes = baos.toByteArray();
        return new MockMultipartFile(originalFilename, originalFilename, contentType, imageBytes);
    }
}
