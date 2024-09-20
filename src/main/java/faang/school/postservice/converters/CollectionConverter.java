package faang.school.postservice.converters;

import java.util.List;
import java.util.stream.StreamSupport;

public class CollectionConverter {

    public static <T> List<T> toList(final Iterable<T> iterable){
        return StreamSupport.stream(iterable.spliterator(),false).toList();
    }

}
