/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.gcolin.jmx.console.example;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * A listener that register a new JMX bean.
 *
 * @author Gael COLIN
 *
 */
public class ExampleListener implements ServletContextListener, ExampleBean {

    private int nb;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("net.gcolin.jmx.console.example:type=ExampleBean");
            if (!mbs.isRegistered(name)) {
                StandardMBean mbean = new StandardMBean(this, ExampleBean.class);
                mbs.registerMBean(mbean, name);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "cannot register mbean", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("net.gcolin.jmx.console.example:type=ExampleBean");
            if (mbs.isRegistered(name)) {
                mbs.unregisterMBean(name);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "cannot register mbean", ex);
        }
    }

    @Override
    public int getNumber() {
        return nb;
    }

    @Override
    public void setNumber(int nb) {
        this.nb = nb;
    }

    @Override
    public String getName() {
        return "I am ExampleListener";
    }

}
