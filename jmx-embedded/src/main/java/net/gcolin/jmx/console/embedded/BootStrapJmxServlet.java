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
package net.gcolin.jmx.console.embedded;

import net.gcolin.jmx.console.JmxHtml;
import net.gcolin.jmx.console.JmxProcessException;
import net.gcolin.jmx.console.JmxResult;
import net.gcolin.jmx.console.JmxTool;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A Jmx servlet with bootstrap style.
 *
 * @author Gael COLIN
 *
 */
public class BootStrapJmxServlet extends HttpServlet {

  private static final long serialVersionUID = 7998004606230901933L;
  protected transient JmxTool tool;
  protected transient JmxHtml html;

  @Override
  public void init() throws ServletException {
    tool = new JmxTool();
    html = new JmxHtml() {
      protected String getButtonCss() {
        return "btn btn-primary";
      }

      protected String getInputTextClass() {
        return "form-control";
      }

      protected String getFormClass() {
        return "form-inline";
      }

      protected String getSelectClass() {
        return "form-control";
      }

      protected String getMenuUlClass() {
        return "menu";
      }

      @Override
      protected String getTableClass() {
        return "table table-bordered";
      }

      protected void writeCss(Writer writer) throws IOException {
        writer.write("<link href=\"/css/bootstrap.min.css\" rel=\"stylesheet\" />");
        writer.write("<link href=\"/css/bootstrap-theme.min.css\" rel=\"stylesheet\" />");
        writer.write("<style type='text/css'>.menu li.active>a{font-weight:bold}"
            + ".col{display:table-cell}.space{padding-left:16px;}</style>");
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    Map<String, String> parameters = new HashMap<String, String>();
    for (Object elt : req.getParameterMap().entrySet()) {
      Entry<String, String[]> entry = (Entry<String, String[]>) elt;
      parameters.put(entry.getKey(), entry.getValue()[0]);
    }
    JmxResult result;
    try {
      result = tool.build(parameters);
    } catch (JmxProcessException ex) {
      throw new ServletException(ex);
    }
    result.setRequestUri(req.getRequestURI());
    result.setQueryParams(req.getQueryString());
    resp.setContentType("text/html");
    html.write(result, parameters, resp.getWriter());
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

}
