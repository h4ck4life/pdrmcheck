package conf;

import connection.ws.WSServlet;
import ninja.servlet.NinjaServletDispatcher;

public class ServletModule extends com.google.inject.servlet.ServletModule {
  
  @Override
  protected void configureServlets() {

    bind(WSServlet.class).asEagerSingleton();
    bind(NinjaServletDispatcher.class).asEagerSingleton();

    filter("/api/ws").through(WSServlet.class);
    serve("/*").with(NinjaServletDispatcher.class);
  }

}
