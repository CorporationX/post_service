package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    ResponseEntity<ResourceResponseDto> uploadResource(Long postId, MultipartFile file);

    ResponseEntity<List<ResourceResponseDto>> uploadImageResource(Long postId, MultipartFile file);

    ResponseEntity<byte[]> downloadResource(Long resourceId);

    ResponseEntity<Void> deleteResource(Long resourceId);
}
