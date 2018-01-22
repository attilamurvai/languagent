package hu.athace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/world")
public class World {
    @GET
    public String getMessage() {
        return "HelloWorld";
    }
}
