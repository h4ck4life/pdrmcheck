package connection.ws;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(name = "WebSocket Servlet", urlPatterns = {"/api/ws"})
public class WSServlet extends WebSocketServlet implements javax.servlet.Filter {

  private static final long serialVersionUID = -6909099107342606245L;

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.getPolicy().setIdleTimeout(10000);
    factory.register(ListenerEchoSocket.class);
    
    System.out.println("websocket configure");
  }
  
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    // TODO Auto-generated method stub
    long startTime = System.currentTimeMillis();
    chain.doFilter(req, res);
    long endTime = System.currentTimeMillis();
    System.out.println(
        "log from DemoServletFilter. request time is: " + (endTime - startTime) + " millis.");
  }

  @Override
  public void init(FilterConfig filter) throws ServletException {
    // TODO Auto-generated method stub

  }

}
