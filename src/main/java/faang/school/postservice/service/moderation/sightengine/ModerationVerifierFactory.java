package faang.school.postservice.service.moderation.sightengine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moderation.targets")
@Data
public class ModerationVerifierFactory {

    private double sexual;
    private double discriminatory;
    private double insulting;
    private double violent;
    private double toxic;

    public ModerationVerifier create() {
        return ModerationVerifier.builder()
                .sexual(sexual)
                .discriminatory(discriminatory)
                .insulting(insulting)
                .violent(violent)
                .toxic(toxic)
                .build();
    }
}
