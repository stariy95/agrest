package com.nhl.link.rest.parser.converter;

import com.nhl.link.rest.runtime.parser.converter.DefaultJsonValueConverterFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

// TODO: these tests check for actual types and break encapsulation
// we might want to think of a better approach
public class DefaultJsonValueConverterFactoryTest {

    @Ignore
    @Test
    public void testConverter() {

        JsonValueConverter<?> c1 = mock(JsonValueConverter.class);
        JsonValueConverter<?> c2 = mock(JsonValueConverter.class);

        DefaultJsonValueConverterFactory factory =
                new DefaultJsonValueConverterFactory(Collections.singletonMap(Long.class, c1), c2);

        assertSame(c1, factory.typedConverter(Long.class));
        assertSame(c2, factory.typedConverter(Long.TYPE));

        assertSame(c2, factory.typedConverter(this.getClass()));
        assertSame(c2, factory.typedConverter(Object.class));
    }

    @Ignore
    @Test
    public void testConverter_Enum() {

        JsonValueConverter<?> c1 = mock(JsonValueConverter.class);
        JsonValueConverter<?> c2 = mock(JsonValueConverter.class);

        DefaultJsonValueConverterFactory factory =
                new DefaultJsonValueConverterFactory(Collections.emptyMap(), c2);

        assertSame(c2, factory.typedConverter(Object.class));


        JsonValueConverter<?> e1c  = factory.typedConverter(E1.class);
        assertTrue(e1c instanceof EnumConverter);
        assertSame(E1.class, ((EnumConverter) e1c).getEnumType());

        JsonValueConverter<?> e2c  = factory.typedConverter(E2.class);
        assertTrue(e2c instanceof EnumConverter);
        assertSame(E2.class, ((EnumConverter) e2c).getEnumType());

        assertSame(e1c, factory.typedConverter(E1.class));
    }

    public enum E1 {
        e11, e12
    }

    public enum E2 {
        e21, e22
    }
}
