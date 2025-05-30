package faang.school.postservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateTest {
    Calculate calculate = new Calculate();
    @Test
    void addTest(){
        assertEquals(5,calculate.add(2,3));
    }

}