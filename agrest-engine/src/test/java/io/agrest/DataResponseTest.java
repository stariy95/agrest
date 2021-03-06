package io.agrest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class DataResponseTest {

    @Test
    public void testForType() {
        DataResponse<Tr> response = DataResponse.forType(Tr.class);
        assertNotNull(response);

        List<Tr> objects = asList(new Tr(), new Tr());
        response.setObjects(objects);
        assertEquals(objects, response.getObjects());
    }

    public static class Tr {
    }
}
