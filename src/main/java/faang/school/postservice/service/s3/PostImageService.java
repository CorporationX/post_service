package faang.school.postservice.service.s3;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostImageService {
    private static final Integer MAX_FILE_COUNT = 10;
    private final S3ServiceImp s3ServiceImp;

    public List<Resource> uploadImages(MultipartFile[] files) {
        if (files.length > MAX_FILE_COUNT) {
            throw new DataValidationException("Max file count is " + MAX_FILE_COUNT);
        }
        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            byte[] bytes = formatImage(file);
            Resource resource = s3ServiceImp.uploadFiles(file, bytes);
            resources.add(resource);
        }
        return resources;
    }

    public List<Resource> deleteImages(List<Long> deletedFileIds) {
        List<Resource> deletedResources = new ArrayList<>();

        for (Long id : deletedFileIds) {
            Resource resource = s3ServiceImp.deleteResource(id);
            deletedResources.add(resource);
        }
        return deletedResources;
    }

    private byte[] formatImage(MultipartFile image) {
        try {
            InputStream inputStream = image.getInputStream();
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                return new byte[0];
            }
            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            if (height > 1080 || width > 1080) {
                if (height == width) {
                    height = 1080;
                    width = 1080;
                } else if (height > width) {
                    height = 1080;
                    width = (1080 * width) / height;
                } else {
                    height = (1080 * height) / width;
                    width = 1080;
                }
            }
            BufferedImage formatImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = formatImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, width, height, null);
            graphics2D.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(formatImage, "jpg", outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error reading image - " + e.getMessage());
        }
    }
}
