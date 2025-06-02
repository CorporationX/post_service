package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.CommentAlreadyHasPictureException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.PictureSize;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.S3Servce;
import faang.school.postservice.util.ImageResizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentResourceService {


    private final CommentRepository commentRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Servce s3Servce;
    private final ImageResizer imageResizer;


    @Value("${commentServiceFileLimitMb}")
    private long fileLimitMb;

    public List<ResourceDto> addResourceToComment(long commentId, MultipartFile file)
            throws FileSizeLimitExceededException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        checkHasPictureAlready(comment);
        checkFileSize(file);
        long postId = comment.getPost().getId();
        String path = "Post" + postId + "Comment" + commentId;

        Resource largeImage = s3Servce.uploadFile
                (imageResizer.getResizedImageStream(file, PictureSize.LARGE), path + "Large");
        Resource smallImage = s3Servce.uploadFile
                (imageResizer.getResizedImageStream(file, PictureSize.SMALL), path + "Small");
//        smallImage.setPost(post);
        smallImage.setComment(comment);
//        largeImage.setPost(post);
//        чтобы при получении поста в его ресурсах не значились картинки комментариев
        largeImage.setComment(comment);

        resourceRepository.save(smallImage);
        resourceRepository.save(largeImage);

        comment.setSmallImageFileKey(smallImage.getKey());
        comment.setLargeImageFileKey(largeImage.getKey());
        commentRepository.save(comment);

        return resourceMapper.toDtos(List.of(smallImage, largeImage));
    }

    private void checkFileSize(MultipartFile file) throws FileSizeLimitExceededException {
        long fileLimitBytes = DataSize.ofMegabytes(fileLimitMb).toBytes();
        if (file.getSize() > fileLimitBytes) {
            throw new FileSizeLimitExceededException("The file must be smaller", file.getSize(), fileLimitBytes);
        }
    }

    private void checkHasPictureAlready(Comment comment) {
        if (comment.getLargeImageFileKey() != null || comment.getSmallImageFileKey() != null) {
            throw new CommentAlreadyHasPictureException
                    ("This comment already has a picture. Delete it before uploading new one");
        }
    }
}
