package net.gcolin.jmx.console.embedded;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class JmxEmbeddedTest {

  public static void main(String[] args) throws Exception {
    JmxEmbedded embedded = new JmxEmbedded(8080);
    
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    ObjectName name = new ObjectName("test:type=EmbeddedServer");
    StandardMBean mbean = new StandardMBean(embedded, AutoCloseable.class);
    mbs.registerMBean(mbean, name);
    
    embedded.join();
  }
  
}
