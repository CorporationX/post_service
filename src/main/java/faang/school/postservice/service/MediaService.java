package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    List<Resource> uploadMedia(List<MultipartFile> files);
    void deleteMedia(List<Resource> deleteList);
 }
