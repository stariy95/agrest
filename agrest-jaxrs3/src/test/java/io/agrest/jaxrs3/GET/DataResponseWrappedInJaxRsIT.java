package io.agrest.jaxrs3.GET;

import io.agrest.DataResponse;
import io.agrest.HttpStatus;
import io.agrest.jaxrs3.junit.AgPojoTester;
import io.agrest.jaxrs3.junit.PojoTest;
import io.bootique.junit5.BQTestTool;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataResponseWrappedInJaxRsIT extends PojoTest {

    @BQTestTool
    static final AgPojoTester tester = PojoTest.tester(Resource.class).build();

    @Test
    public void basic() {
        tester.target("/wrapped-data-response")
                .get()
                .wasOk()
                .bodyEquals(2,
                        "{\"a\":1}",
                        "{\"a\":100}");
    }

    @Test
    public void implicitDataResponseStatusIgnored() {
        tester.target("/wrapped-data-response/implicit-data-response-status-ignored")
                .get()
                .wasStatus(Response.Status.PARTIAL_CONTENT.getStatusCode())
                .bodyEquals(2,
                        "{\"a\":1}",
                        "{\"a\":100}");
    }

    @Test
    public void explicitDataResponseStatusIgnored() {
        tester.target("/wrapped-data-response/explicit-data-response-status-ignored")
                .get()
                .wasStatus(Response.Status.PARTIAL_CONTENT.getStatusCode())
                .bodyEquals(2,
                        "{\"a\":1}",
                        "{\"a\":100}");
    }

    @Path("wrapped-data-response")
    public static class Resource {

        @GET
        public Response basic() {
            DataResponse<X> dr = DataResponse.of(List.of(
                    new X(1),
                    new X(100))).build();

            return Response.ok(dr).build();
        }

        @GET
        @Path("implicit-data-response-status-ignored")
        public Response implicitDataResponseStatusIgnored() {
            DataResponse<X> dr = DataResponse.of(List.of(
                    new X(1),
                    new X(100))).build();

            return Response
                    // implicit DataResponse status is ignored, JAX-RS status is used
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .entity(dr)
                    .build();
        }

        @GET
        @Path("explicit-data-response-status-ignored")
        public Response explicitDataResponseStatusIgnored() {
            DataResponse<X> dr = DataResponse.of(List.of(
                    new X(1),
                    new X(100)))
                    // explicitly set status
                    .status(HttpStatus.OK)
                    .build();

            return Response
                    // explicit DataResponse status is ignored, JAX-RS status is used
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .entity(dr)
                    .build();
        }
    }

    public static class X {
        private final int a;

        public X(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }
    }
}