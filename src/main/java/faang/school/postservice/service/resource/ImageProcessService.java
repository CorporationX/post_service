package faang.school.postservice.service.resource;

import faang.school.postservice.model.ImageType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessService {

    byte[] resizeImage(MultipartFile originalFile, ImageType imageType);
}