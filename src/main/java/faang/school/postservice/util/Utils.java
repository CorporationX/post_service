package faang.school.postservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.FormattedMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utils {
    public String format(final String messagePattern, final Object arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object arg1, final Object arg2) {
        return new FormattedMessage(messagePattern, arg1, arg2).getFormattedMessage();
    }

    public String format(final String messagePattern, final Object... arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }
}
