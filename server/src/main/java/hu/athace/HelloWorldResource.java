package hu.athace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/hello/{name}")
public class HelloWorldResource {

    @GET
    public String getMessage(@PathParam("name") String name) {
        return "Hello " + name;
    }
}
