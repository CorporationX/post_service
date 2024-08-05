package faang.school.postservice.api;

import faang.school.postservice.dto.media.MediaDto;
import org.springframework.web.multipart.MultipartFile;

public interface MultipartFileMediaApi extends MediaApi<String, MediaDto, MultipartFile> {
}
