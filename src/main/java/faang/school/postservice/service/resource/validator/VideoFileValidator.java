package faang.school.postservice.service.resource.validator;

public class VideoFileValidator extends AbstractFileValidator {

    public VideoFileValidator(long maxSize, int maxInPost) {
        super(maxSize, maxInPost);
    }
}
