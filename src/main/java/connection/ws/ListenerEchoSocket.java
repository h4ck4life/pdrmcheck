package connection.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class ListenerEchoSocket implements WebSocketListener {

  private Session outbound;

  @Override
  public void onWebSocketBinary(byte[] payload, int offset, int len) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onWebSocketClose(int statusCode, String reason) {
    // TODO Auto-generated method stub
    this.outbound = null;
  }

  @Override
  public void onWebSocketConnect(Session session) {
    // TODO Auto-generated method stub
    this.outbound = session;
  }

  @Override
  public void onWebSocketError(Throwable cause) {
    // TODO Auto-generated method stub
    cause.printStackTrace(System.err);
  }

  @Override
  public void onWebSocketText(String message) {
    // TODO Auto-generated method stub
    if ((outbound != null) && (outbound.isOpen())) {
      System.out.printf("Echoing back message [%s]%n", message);
      // echo the message back
      outbound.getRemote().sendString(message, null);
    }
  }

}
