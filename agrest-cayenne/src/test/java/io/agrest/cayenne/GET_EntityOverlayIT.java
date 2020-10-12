package io.agrest.cayenne;

import io.agrest.Ag;
import io.agrest.DataResponse;
import io.agrest.MetadataResponse;
import io.agrest.annotation.AgAttribute;
import io.agrest.cayenne.cayenne.main.*;
import io.agrest.cayenne.unit.AgCayenneTester;
import io.agrest.cayenne.unit.DbTest;
import io.agrest.meta.AgEntity;
import io.agrest.meta.AgEntityOverlay;
import io.agrest.runtime.AgBuilder;
import io.bootique.junit5.BQTestTool;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectId;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GET_EntityOverlayIT extends DbTest {

    @BQTestTool
    static final AgCayenneTester tester = tester(Resource.class)
            .entities(E2.class, E3.class, E4.class, E7.class, E8.class)
            .agCustomizer(GET_EntityOverlayIT::addOverlay)
            .build();

    private static AgBuilder addOverlay(AgBuilder builder) {

        AgEntityOverlay<E2> e2Overlay = AgEntity.overlay(E2.class)
                .redefineAttribute("adhocString", String.class, e2 -> e2.getName() + "*")
                .exclude("address");

        AgEntityOverlay<E4> e4Overlay = AgEntity.overlay(E4.class)
                .redefineAttribute("adhocString", String.class, e4 -> e4.getCVarchar() + "*")
                .redefineToOne("adhocToOne", EX.class, EX::forE4)
                .redefineToMany("adhocToMany", EY.class, EY::forE4)
                .redefineAttribute("derived", String.class, E4::getDerived);

        AgEntityOverlay<E7> e7Overlay = AgEntity.overlay(E7.class)
                .redefineRelationshipResolver("e8", e7 -> {
                    E8 e8 = new E8();
                    e8.setObjectId(new ObjectId("e8", "id", Cayenne.intPKForObject(e7)));
                    e8.setName(e7.getName() + "_e8");
                    return e8;
                })
                // we are changing the type of the existing attribute
                .redefineAttribute("name", Integer.class, e7 -> e7.getName().length());

        return builder.entityOverlay(e4Overlay).entityOverlay(e2Overlay).entityOverlay(e7Overlay);
    }

    @Test
    public void testOverlayMeta() {

        String data = tester.target("/e4/meta").get().wasOk().getContentAsString();

        assertTrue(data.contains("{\"name\":\"derived\",\"type\":\"string\"}"));
        assertTrue(data.contains("{\"name\":\"adhocString\",\"type\":\"string\"}"));
        assertTrue(data.contains("{\"name\":\"adhocToOne\",\"type\":\"EX\",\"relationship\":true}"));
        assertTrue(data.contains("{\"name\":\"adhocToMany\",\"type\":\"EY\",\"relationship\":true,\"collection\":true}"));
    }

    @Test
    public void testExclude() {

        tester.e2().insertColumns("id_", "name", "address").values(1, "N", "A").exec();

        tester.target("/e2")
                .get()
                .wasOk()
                .bodyEquals(1, "{\"id\":1,\"adhocString\":\"N*\",\"name\":\"N\"}");
    }

    @Test
    public void testRedefineAttribute_Transient() {

        tester.e4().insertColumns("id", "c_varchar")
                .values(1, "x")
                .values(2, "y").exec();

        tester.target("/e4")
                .queryParam("include", "derived")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2, "{\"derived\":\"x$\"},{\"derived\":\"y$\"}");
    }

    @Test
    public void testRedefineAttribute_AdHocRelated() {

        tester.e2().insertColumns("id_", "name").values(1, "xxx").exec();
        tester.e3().insertColumns("id_", "name", "e2_id").values(3, "zzz", 1).exec();

        tester.target("/e3")
                .queryParam("include", "id")
                .queryParam("include", "e2.adhocString")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(1, "{\"id\":3,\"e2\":{\"adhocString\":\"xxx*\"}}");
    }

    @Test
    public void testRedefineAttribute_AdHoc() {

        tester.e4().insertColumns("id", "c_varchar")
                .values(1, "x")
                .values(2, "y").exec();

        tester.target("/e4")
                .queryParam("include", "adhocString")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2, "{\"adhocString\":\"x*\"},{\"adhocString\":\"y*\"}");
    }

    @Test
    public void testRedefineToOne_AdHoc() {

        tester.e4().insertColumns("id", "c_varchar")
                .values(1, "x")
                .values(2, "y").exec();

        tester.target("/e4")
                .queryParam("include", "id")
                .queryParam("include", "adhocToOne")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2,
                "{\"id\":1,\"adhocToOne\":{\"p1\":\"x_\"}}",
                "{\"id\":2,\"adhocToOne\":{\"p1\":\"y_\"}}");
    }

    @Test
    public void testRedefineToMany_AdHoc() {

        tester.e4().insertColumns("id", "c_varchar")
                .values(1, "x")
                .values(2, "y").exec();

        tester.target("/e4")
                .queryParam("include", "id")
                .queryParam("include", "adhocToMany")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2,
                "{\"id\":1,\"adhocToMany\":[{\"p1\":\"x-\"},{\"p1\":\"x%\"}]}",
                "{\"id\":2,\"adhocToMany\":[{\"p1\":\"y-\"},{\"p1\":\"y%\"}]}");
    }

    @Test
    public void testRedefineToOne_Replaced() {

        tester.e7().insertColumns("id", "name")
                .values(1, "x1")
                .values(2, "x2").exec();

        tester.target("/e7")
                .queryParam("include", "id")
                .queryParam("include", "e8.name")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2,
                "{\"id\":1,\"e8\":{\"name\":\"x1_e8\"}}",
                "{\"id\":2,\"e8\":{\"name\":\"x2_e8\"}}");
    }

    @Test
    public void testRedefineAttribute_Replaced() {

        tester.e7().insertColumns("id", "name")
                .values(1, "01")
                .values(2, "0123").exec();

        tester.target("/e7")
                .queryParam("include", "id")
                .queryParam("include", "name")
                .queryParam("sort", "id")
                .get().wasOk().bodyEquals(2,
                "{\"id\":1,\"name\":2}",
                "{\"id\":2,\"name\":4}");
    }

    public static final class EX {

        private final String p1;

        public EX(String p1) {
            this.p1 = p1;
        }

        static EX forE4(E4 e4) {
            return new EX(e4.getCVarchar() + "_");
        }

        @AgAttribute
        public String getP1() {
            return p1;
        }
    }

    public static final class EY {

        private final String p1;

        public EY(String p1) {
            this.p1 = p1;
        }

        static List<EY> forE4(E4 e4) {
            return asList(
                    new EY(e4.getCVarchar() + "-"),
                    new EY(e4.getCVarchar() + "%")
            );
        }

        @AgAttribute
        public String getP1() {
            return p1;
        }
    }

    @Path("")
    public static final class Resource {
        @Context
        private Configuration config;

        @GET
        @Path("e2")
        public DataResponse<E2> getE2(@Context UriInfo uriInfo) {
            return Ag.service(config).select(E2.class).uri(uriInfo).get();
        }

        @GET
        @Path("e3")
        public DataResponse<E3> getE3(@Context UriInfo uriInfo) {
            return Ag.service(config).select(E3.class).uri(uriInfo).get();
        }

        @GET
        @Path("e4")
        public DataResponse<E4> getE4(@Context UriInfo uriInfo) {
            return Ag.service(config).select(E4.class).uri(uriInfo).get();
        }

        @GET
        @Path("e4/meta")
        public MetadataResponse<E4> getMetaE4(@Context UriInfo uriInfo) {
            return Ag.metadata(E4.class, config).forResource(Resource.class).uri(uriInfo).process();
        }

        @GET
        @Path("e7")
        public DataResponse<E7> getE7(@Context UriInfo uriInfo) {
            return Ag.service(config).select(E7.class).uri(uriInfo).get();
        }
    }
}
