package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.CommentAlreadyHasPictureException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.S3Servce;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentResourceService {


    private final CommentRepository commentRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Servce s3Servce;

    @Value("${commentServiceFileLimitMb}")
    private long fileLimitMb;

    public List<ResourceDto> addResourceToComment(long commentId, MultipartFile file)
            throws FileSizeLimitExceededException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        if (comment.getLargeImageFileKey() != null || comment.getSmallImageFileKey() != null) {
            throw new CommentAlreadyHasPictureException
                    ("This comment already has a picture. Delete it before uploading new one");
        }
        long fileLimitBytes = DataSize.ofMegabytes(fileLimitMb).toBytes();
        if (file.getSize() > fileLimitBytes) {
            throw new FileSizeLimitExceededException("The file must be shorter", file.getSize(), fileLimitBytes);
        }
        Post post = comment.getPost();
        String folder = "Post" + post.getId();
        Resource smallImage = s3Servce.uploadSmallFile(file, folder);
        Resource largeImage = s3Servce.uploadLargeFile(file, folder);
        smallImage.setPost(post);
        smallImage.setComment(comment);
        largeImage.setPost(post);
        largeImage.setComment(comment);

        comment.setSmallImageFileKey(smallImage.getKey());
        comment.setLargeImageFileKey(largeImage.getKey());

        return List.of(smallImage, largeImage);
    }
}
