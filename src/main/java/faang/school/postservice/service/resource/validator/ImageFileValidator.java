package faang.school.postservice.service.resource.validator;


public class ImageFileValidator extends AbstractFileValidator {

    public ImageFileValidator(long maxSize, int maxInPost) {
        super(maxSize, maxInPost);
    }
}
