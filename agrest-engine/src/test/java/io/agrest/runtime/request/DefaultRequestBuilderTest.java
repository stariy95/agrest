package io.agrest.runtime.request;

import io.agrest.AgException;
import io.agrest.AgRequest;
import io.agrest.protocol.Dir;
import io.agrest.protocol.Sort;
import io.agrest.protocol.exp.SimpleExp;
import io.agrest.runtime.jackson.IJacksonService;
import io.agrest.runtime.jackson.JacksonService;
import io.agrest.runtime.protocol.ExcludeParser;
import io.agrest.runtime.protocol.ExpParser;
import io.agrest.runtime.protocol.IExcludeParser;
import io.agrest.runtime.protocol.IExpParser;
import io.agrest.runtime.protocol.IIncludeParser;
import io.agrest.runtime.protocol.ISizeParser;
import io.agrest.runtime.protocol.ISortParser;
import io.agrest.runtime.protocol.IncludeParser;
import io.agrest.runtime.protocol.SizeParser;
import io.agrest.runtime.protocol.SortParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultRequestBuilderTest {

    DefaultRequestBuilder builder;

    @BeforeEach
    public void beforeEach() {

        IJacksonService jacksonService = new JacksonService();

        // prepare parse request stage
        IExpParser expParser = new ExpParser(jacksonService);
        ISortParser sortParser = new SortParser(jacksonService);
        ISizeParser sizeParser = new SizeParser();
        IIncludeParser includeParser = new IncludeParser(jacksonService, expParser, sortParser, sizeParser);
        IExcludeParser excludeParser = new ExcludeParser(jacksonService);

        this.builder = new DefaultRequestBuilder(expParser, sortParser, includeParser, excludeParser);
    }

    @Test
    public void testBuild_Defaults() {

        AgRequest request = builder.build();

        assertNotNull(request);
        assertNull(request.getExp());
        assertTrue(request.getOrderings().isEmpty());
        assertNull(request.getMapBy());
        assertNull(request.getLimit());
        assertNull(request.getStart());
        assertTrue(request.getIncludes().isEmpty());
        assertTrue(request.getExcludes().isEmpty());
    }

    @Test
    public void testBuild_Include() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put("include", Arrays.asList("a", "b"));

        AgRequest request = builder
                .mergeClientParams(params)
                .build();

        assertEquals(2, request.getIncludes().size());
        assertEquals("a", request.getIncludes().get(0).getPath());
        assertEquals("b", request.getIncludes().get(1).getPath());
    }

    @Test
    public void testBuild_Include_Array() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("include", "[\"a\", \"b\"]");
        AgRequest request = builder.mergeClientParams(params).build();

        assertEquals(2, request.getIncludes().size());
        assertEquals("a", request.getIncludes().get(0).getPath());
        assertEquals("b", request.getIncludes().get(1).getPath());
    }

    @Test
    public void testBuild_Exclude() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put("exclude", Arrays.asList("a", "b"));
        AgRequest request = builder.mergeClientParams(params).build();

        assertEquals(2, request.getExcludes().size());
        assertEquals("a", request.getExcludes().get(0).getPath());
        assertEquals("b", request.getExcludes().get(1).getPath());
    }

    @Test
    public void testBuild_Exclude_Array() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("exclude", "[\"a\", \"b\"]");
        AgRequest request = builder.mergeClientParams(params).build();

        assertEquals(2, request.getExcludes().size());
        assertEquals("a", request.getExcludes().get(0).getPath());
        assertEquals("b", request.getExcludes().get(1).getPath());
    }

    @Test
    public void testBuild_IncludeExclude() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put("include", Arrays.asList("a", "b", "id"));
        params.put("exclude", Arrays.asList("a", "c"));
        AgRequest request = builder.mergeClientParams(params).build();

        assertEquals(3, request.getIncludes().size());
        assertEquals("a", request.getIncludes().get(0).getPath());
        assertEquals("b", request.getIncludes().get(1).getPath());
        assertEquals("id", request.getIncludes().get(2).getPath());

        assertEquals(2, request.getExcludes().size());
        assertEquals("a", request.getExcludes().get(0).getPath());
        assertEquals("c", request.getExcludes().get(1).getPath());
    }

    @Test
    public void testBuild_IncludeRels() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("include", "rtss");
        AgRequest request = builder.mergeClientParams(params).build();

        assertEquals(1, request.getIncludes().size());
        assertEquals("rtss", request.getIncludes().get(0).getPath());
    }

    @Test
    public void testBuild_SortSimple_NoDir() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "rtss");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getOrderings());
        Sort ordering = request.getOrderings().get(0);
        assertEquals("rtss", ordering.getProperty());
    }

    @Test
    public void testBuild_SortSimple_ASC() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "rtss");
        params.putSingle("dir", "ASC");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getOrderings());
        assertEquals(1, request.getOrderings().size());
        Sort ordering = request.getOrderings().get(0);
        assertEquals("rtss", ordering.getProperty());
        assertEquals(Dir.ASC, ordering.getDirection());
    }

    @Test
    public void testBuild_SortSimple_DESC() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "rtss");
        params.putSingle("dir", "DESC");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getOrderings());

        Sort ordering = request.getOrderings().get(0);
        assertEquals("rtss", ordering.getProperty());
        assertEquals(Dir.DESC, ordering.getDirection());
    }

    @Test
    public void testBuild_SortSimple_Garbage() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "xx");
        params.putSingle("dir", "XYZ");

        assertThrows(AgException.class, () -> builder.mergeClientParams(params).build());
    }

    @Test
    public void testBuild_Sort() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "[{\"property\":\"a\",\"direction\":\"DESC\"},{\"property\":\"b\",\"direction\":\"ASC\"}]");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getOrderings());
        assertEquals(2, request.getOrderings().size());

        Sort o1 = request.getOrderings().get(0);
        Sort o2 = request.getOrderings().get(1);

        assertEquals("a", o1.getProperty());
        assertEquals(Dir.DESC, o1.getDirection());
        assertEquals("b", o2.getProperty());
        assertEquals(Dir.ASC, o2.getDirection());
    }

    @Test
    public void testBuild_Sort_Dupes() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "[{\"property\":\"a\",\"direction\":\"DESC\"},{\"property\":\"a\",\"direction\":\"ASC\"}]");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getOrderings());
        assertEquals(2, request.getOrderings().size());

        Sort o1 = request.getOrderings().get(0);
        Sort o2 = request.getOrderings().get(1);

        assertEquals("a", o1.getProperty());
        assertEquals(Dir.DESC, o1.getDirection());
        assertEquals("a", o2.getProperty());
        assertEquals(Dir.ASC, o2.getDirection());
    }

    @Test
    public void testBuild_Sort_BadSpec() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("sort", "[{\"property\":\"p1\",\"direction\":\"DESC\"},{\"property\":\"p2\",\"direction\":\"XXX\"}]");

        assertThrows(AgException.class, () -> builder.mergeClientParams(params).build());
    }

    @Test
    public void testBuild_Exp_BadSpec() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("exp", "{exp : \"numericProp = 12345 and stringProp = 'John Smith' and booleanProp = true\"}");

        assertThrows(AgException.class, () -> builder.mergeClientParams(params).build());
    }

    @Test
    public void testBuild_Exp() {

        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.putSingle("exp", "{\"exp\" : \"a = 'John Smith'\"}");
        AgRequest request = builder.mergeClientParams(params).build();

        assertNotNull(request.getExp());

        assertEquals("a = 'John Smith'", ((SimpleExp) request.getExp()).getTemplate());
    }
}
