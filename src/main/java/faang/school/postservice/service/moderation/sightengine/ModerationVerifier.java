package faang.school.postservice.service.moderation.sightengine;

import faang.school.postservice.service.moderation.sightengine.check.ModerationCheck;
import faang.school.postservice.service.moderation.sightengine.check.impl.BaseCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationVerifier {

    private double sexual;
    private double discriminatory;
    private double insulting;
    private double violent;
    private double toxic;

    private final List<ModerationCheck> checks = new ArrayList<>();

    public boolean verify() {
        for (ModerationCheck check : checks) {
            if (!check.check()) return false;
        }
        return true;
    }

    public ModerationVerifier sexual(double value) {
        checks.add(new BaseCheck(value, sexual));
        return this;
    }

    public ModerationVerifier discriminatory(double value) {
        checks.add(new BaseCheck(value, discriminatory));
        return this;
    }

    public ModerationVerifier insulting(double value) {
        checks.add(new BaseCheck(value, insulting));
        return this;
    }

    public ModerationVerifier violent(double value) {
        checks.add(new BaseCheck(value, violent));
        return this;
    }

    public ModerationVerifier toxic(double value) {
        checks.add(new BaseCheck(value, toxic));
        return this;
    }
}
