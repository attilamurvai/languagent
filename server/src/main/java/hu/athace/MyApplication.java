package hu.athace;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class MyApplication
        extends Application {

    // todo: empty vs getClasses vs getSingletons
    // not overriding getClasses adds all the classes annotated with @Path
//    @Override
//    public Set<Class<?>> getClasses() {
//        return new HashSet<>(Arrays.asList(new Class<?>[]{HelloWorldResource.class}));
//    }
}
