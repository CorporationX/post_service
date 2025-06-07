package faang.school.postservice.service;

import faang.school.postservice.model.ad.PictureSize;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.expression.AccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentFileService {

    List<String> addImageToComment(long commentId, MultipartFile file)
            throws FileSizeLimitExceededException, AccessException;

    byte[] getCommentImage(Long commentId, String size);

    void deleteImageFromCommentById(Long commentId) throws AccessException;
}
