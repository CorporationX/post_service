package faang.school.postservice.converters;

import java.util.List;
import java.util.stream.StreamSupport;

public class CollectionConverter {

    public static <T> List<T> toList(final Iterable<T> iterable){
        return StreamSupport.stream(iterable.spliterator(),false).toList();
    }

    public static int divideToParts(long amount, int limit){
        double order = Math.log10(amount);
        int result = (int)(amount/(order*Math.pow(10,order/2)));
        if (result > limit)  {
            int k = result/limit;
            result /= k;
        }
        return result;
    }

}
