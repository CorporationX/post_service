package faang.school.postservice.job.moderation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class SwearingPostModeratorTest {

    private SwearingPostModerator swearingPostModerator = new SwearingPostModerator(List.of("swearing1", "swearing2"));

    @ParameterizedTest
    @MethodSource("faang.school.postservice.job.moderation.SwearingPostModeratorTest#getArgumentsForTestCheckVerified")
    void testCheckVerified(String content, boolean expectedResult) {

     //   boolean actualResult = swearingPostModerator.checkVerified(content);
    //    System.out.println(actualResult);

    //    Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> getArgumentsForTestCheckVerified() {
        return Stream.of(
                Arguments.of("asdsadsa asd", true),
                Arguments.of("asdsadsa aa swearing1 aaasd", false),
                Arguments.of("asdsadsa swearing2aaasd", false)
        );
    }
}