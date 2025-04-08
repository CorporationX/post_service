package faang.school.postservice.service.thumbnails;

import java.util.Map;

public interface ImageResize {

    Map<String, byte[]> getResizedImages(byte[] imageBytes);
}
