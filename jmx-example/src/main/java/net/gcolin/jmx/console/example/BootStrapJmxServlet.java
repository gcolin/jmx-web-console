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

import net.gcolin.jmx.console.JmxHtml;
import net.gcolin.jmx.console.JmxTool;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;

/**
 * A Jmx servlet with bootstrap style.
 *
 * @author Gael COLIN
 *
 */
public class BootStrapJmxServlet extends SimpleJmxServlet {

    private static final long serialVersionUID = 1126427599397867258L;

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

            @Override
            protected void writeTopMenu(Writer writer) throws IOException {
                writer.write("<nav class='navbar navbar-default'><div class='container-fluid'>" + 
                		"<div class='navbar-header'><a class='navbar-brand' href='");
                writer.write(getBasePath(getServletContext().getContextPath()));
                writer.write("/'><span class='glyphicon glyphicon-home'></span> Home</a></div></nav>");
            }

            protected void writeTitle(Writer writer) throws IOException {
                super.writeTitle(writer);
                writer.write("<p>A Jmx servlet with Bootstrap style.</p>");
            }

            protected void writeCss(Writer writer) throws IOException {
                writer.write("<link href=\"");
                writer.write(getBasePath(getServletContext().getContextPath()));
                writer.write("/css/bootstrap.min.css\" rel=\"stylesheet\" />");
                writer.write("<link href=\"");
                writer.write(getBasePath(getServletContext().getContextPath()));
                writer.write("/css/bootstrap-theme.min.css\" rel=\"stylesheet\" />");
                writer.write(
                        "<style type='text/css'>.menu li.active>a{font-weight:bold}"
                        + ".col{display:table-cell}.space{padding-left:16px;}</style>");
            }
        };
    }

}
