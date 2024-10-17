package faang.school.postservice.service.resource.validator;


public class AudioFileValidator extends AbstractFileValidator {

    public AudioFileValidator(long maxSize, int maxInPost) {
        super(maxSize, maxInPost);
    }
}
