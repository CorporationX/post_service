package faang.school.postservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.MyMultipartFile;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final PostRepository postRepository;
    private final AmazonS3 clientAmazonS3;
    public final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostService postService;


    @Transactional
    public void addResource(long postId, MultipartFile file) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new DataValidationException("Post with id " + postId + " not found.");
        }
        Post post = optionalPost.get();
        byte[] compressedImage = null;
        // Проверяем размер файла до сжатия
//        if (file.getSize() > 5 * 1024 * 1024) {
//            compressedImage = s3Service.compressImageSize(file);
//        }
//        if(){}
        String folder = post.getId() + "/" + file.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        // Используем сжатые данные, если они доступны, иначе используем оригинальный файл
//        if (compressedImage != null) {
//            // Создаем MultipartFile из сжатых данных
//            MultipartFile compressedFile = new MyMultipartFile(
//                    file.getName(), file.getOriginalFilename(), file.getContentType(), compressedImage);
//            resource = s3Service.uploadFile(compressedFile, folder);
//        } else {
//            resource = ;
//        }

        resource.setPost(post);
        resource = resourceRepository.save(resource);
        log.info("File saved");

        post.getResources().add(resource);
        postRepository.save(post);
    }
}
