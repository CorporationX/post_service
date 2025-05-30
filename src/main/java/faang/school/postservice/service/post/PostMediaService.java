package faang.school.postservice.service.post;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.utils.PostServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMediaService {
    private final PostServiceUtils postServiceUtils;
    private final S3Client s3Client;

    public PostDto addImages(Long postId, MultipartFile file) {
        Post post = postServiceUtils.checkPostExists(postId);

//        BigInteger newStorageSize = post.getStorageSize().add(BigInteger.valueOf(file.getSize()));
//        checkStorageSizeExceeded(newStorageSize, post.getMaxStorageSize());

        String folder = "post" + post.getId();

    }
}
