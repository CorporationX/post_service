package faang.school.postservice.service;

import faang.school.postservice.config.AwsS3ApiConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileService {
    private final AwsS3ApiConfig awsS3ApiConfig;
    private final S3Service s3Service;
    private final PostService postService;

    @Value("${file-controller.upload.max-files-size-in-post-mb}")
    private long maxFileSizeMb;

    @Value("${file-controller.image.max-width}")
    private int maxWidth;

    @Value("${file-controller.image.max-height-rectangle}")
    private int maxHeightRectangle;

    public List<String> uploadFiles(Long postId, List<MultipartFile> files) {
        List<String> fileKeys = new ArrayList<>();

        files.forEach(file -> {
            validateFile(file);
            try {
                byte[] fileBytes = processFile(file);
                String key = uploadFileToS3(postId, file, fileBytes);
                fileKeys.add(key);
            } catch (IOException e) {
                fileKeys.forEach(key -> s3Service.deleteFileAsync(awsS3ApiConfig.getBucket(),key).join());
                throw new RuntimeException("Failed to process file", e);
            }
        });

        updatePostWithResources(postId, fileKeys);

        return fileKeys;
    }

    public void deleteFiles(List<String> fileIds) {
        List<Post> postsToUpdate = postService.findPostsByResourceKeys(fileIds);
        for (Post post : postsToUpdate) {
            List<Resource> resources = new ArrayList<>(post.getResources());
            resources.removeIf(resource -> fileIds.contains(resource.getKey()));
            post.setResources(resources);
            postService.update(post);
        }
        for (String fileId : fileIds) {
            s3Service.deleteFileAsync(awsS3ApiConfig.getBucket(), fileId).join();
        }
    }

    public String getPresignedUrl(String fileId) {
        return s3Service.createPresignedGetUrl(awsS3ApiConfig.getBucket(), fileId);
    }

    public byte[] getObjectBytes(String fileId) {
        return s3Service.getObjectBytes(awsS3ApiConfig.getBucket(), fileId);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxFileSizeMb * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than or equal to 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("video/")
                && !contentType.startsWith("audio/")
                && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private byte[] processFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        BufferedImage image = null;

        if (contentType != null && contentType.startsWith("image/")) {
            image = ImageIO.read(file.getInputStream());
            if (image != null) {
                image = resizeImageIfNeeded(image);
            }
        }

        return image != null ? bufferedImageToByteArray(image, contentType) : file.getBytes();
    }

    private String uploadFileToS3(Long postId, MultipartFile file, byte[] fileBytes) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(fileBytes.length));
        metadata.put("Original-Filename", file.getOriginalFilename());
        String key = "post/" + postId + "/" + UUID.randomUUID();
        PutObjectResponse result = s3Service.uploadFileAsync(awsS3ApiConfig.getBucket(), key, metadata, fileBytes).join();
        return result.eTag();
    }

    private void updatePostWithResources(Long postId, List<String> fileKeys) {
        Post postToUpdate = postService.get(postId);
        List<Resource> resources = postToUpdate.getResources();
        fileKeys.forEach(key -> {
            resources.add(Resource.builder()
                    .key(key)
                    .post(postToUpdate)
                    .build());
        });
        postService.update(postToUpdate);
    }

    private BufferedImage resizeImageIfNeeded(BufferedImage image) {
        //assuming image size cannot be different
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (width == height) {
            newWidth = Math.min(newWidth, maxWidth);
            newHeight = newWidth;
        } else {
            float aspectRatio = (float) width / height;
            newWidth = Math.min(newWidth, maxWidth);
            newHeight = Math.round(maxWidth / aspectRatio);
            if (newHeight > maxHeightRectangle) {
                newHeight = maxHeightRectangle;
                newWidth = Math.round(newHeight * aspectRatio);
            }
        }

        if (width != newWidth || height != newHeight) {
            Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            //drawImage - resource intensive, replaced with AffineTransform
            //g2d.drawImage(tmp, 0, 0, null);
            AffineTransform at = AffineTransform
                    .getScaleInstance((double) newWidth / width, (double) newHeight / height);
            g2d.drawRenderedImage(image, at);
            g2d.dispose();
            return resized;
        }

        return image;
    }

    private byte[] bufferedImageToByteArray(BufferedImage image, String contentType) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("BufferedImage is null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = contentType != null && contentType.contains("/") ? contentType.split("/")[1] : "png";

        boolean result = ImageIO.write(image, formatName, baos);
        if (!result) {
            throw new IOException("Failed to write image to ByteArrayOutputStream");
        }

        return baos.toByteArray();
    }
}