package net.gcolin.jmx.console.embedded;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JmxEmbedded implements AutoCloseable {

  private Server server;

  public JmxEmbedded(int port) {
    server = new Server(port);
    ServletContextHandler handler = new ServletContextHandler();
    handler.addServlet(BootStrapJmxServlet.class, "");

    String res = this.getClass().getClassLoader().getResource("embeddedresources").toExternalForm();
    handler.addServlet(DefaultServlet.class, "/").setInitParameter("resourceBase", res);

    server.setHandler(handler);
    try {
      server.start();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  public void close() throws Exception {
    server.stop();
  }

  public void join() throws InterruptedException {
    server.join();
  }

}
