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
package net.gcolin.jmx.console;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Format a JmxResult into HTML.
 *
 * @author Gael COLIN
 *
 */
public class JmxHtml {

    /**
     * Write the result in HTML format.
     *
     * @param result result
     * @param parameters parameters
     * @param writer writer
     * @throws IOException if an error occurs.
     */
    public void write(JmxResult result, Map<String, String> parameters, Writer writer)
            throws IOException {
        writeStart(writer);
        writeCss(writer);
        writeTopMenu(writer);
        writeStartAll(writer);
        writeTitle(writer);
        writeStartMenu(writer);
        writeMenu(result, result.getMenu(), writer);
        writeEndMenu(writer);
        writeStartContent(writer);
        writeResponse(result, writer);
        if (result.getCurrentObjectName() != null) {
            writeAttributes(result, writer);
            writeOperations(parameters, result, writer);
        }
        writeEndContent(writer);
        writeEndAll(writer);
        writeEnd(writer);
    }

    protected void writeStart(Writer writer) throws IOException {
        writer.write("<!doctype html><html><head><title>JMX Web console</title>" + "</head><body>");
    }

    protected void writeTopMenu(Writer writer) throws IOException {

    }

    protected void writeStartAll(Writer writer) throws IOException {
        writer.write("<div class='container'>");
    }

    protected void writeEndAll(Writer writer) throws IOException {
        writer.write("</div>");
    }

    protected void writeEnd(Writer writer) throws IOException {
        writer.write("</body></html>");
    }

    protected void writeCss(Writer writer) throws IOException {
        writer.write("<style type='text/css'>li.active>a{font-weight:bold}.col{display:table-cell}"
                + ".table{border-collapse: collapse;}.table,.table th,.table td{border: 1px solid black;"
                + "}.space{padding-left:16px;}</style>");
    }

    protected void writeTitle(Writer writer) throws IOException {
        writer.write("<h1>JMX Web console</h1>");
    }

    protected void writeStartMenu(Writer writer) throws IOException {
        writer.write("<div class='col'>");
    }

    protected void writeEndMenu(Writer writer) throws IOException {
        writer.write("</div>");
    }

    protected void writeStartContent(Writer writer) throws IOException {
        writer.write("<div class='col space'>");
    }

    protected void writeEndContent(Writer writer) throws IOException {
        writer.write("</div>");
    }

    protected String getMenuUlClass() {
        return "";
    }

    protected String getMenuLiActiveClass() {
        return "active";
    }

    protected String getTableClass() {
        return "table";
    }

    protected String getSelectClass() {
        return "";
    }

    protected String getInputTextClass() {
        return "";
    }

    protected String getFormClass() {
        return "";
    }

    protected String getButtonCss() {
        return "";
    }

    protected String getButtonSelectText() {
        return "Select";
    }

    protected String getButtonSendText() {
        return "Send";
    }

    protected String getButtonUpdateText() {
        return "Update";
    }

    protected String getOutputText() {
        return "Output: ";
    }

    protected String getOperationsText() {
        return "Operations";
    }

    protected String getOperationText() {
        return "Operation";
    }

    protected String getAttributesText() {
        return "Attributes";
    }

    protected String getNameText() {
        return "Name";
    }

    protected String getValueText() {
        return "Value";
    }

    protected void writeMenu(JmxResult result, List<JmxMenu> menus, Writer writer)
            throws IOException {
        writer.write("<ul class='");
        writer.write(getMenuUlClass());
        writer.write("'>");
        for (JmxMenu menu : menus) {
            writer.write("<li");
            if (menu.isSelected()) {
                writer.write(" class='");
                writer.write(getMenuLiActiveClass());
                writer.write("'");
            }
            writer.write("><a href='");
            writer.write(result.getRequestUri());
            writer.write("?");
            boolean first = true;
            for (Entry<String, String> param : menu.getParameters().entrySet()) {
                if (first) {
                    first = false;
                } else {
                    writer.write("&");
                }
                writer.write(param.getKey());
                writer.write('=');
                writer.write(param.getValue());
            }
            writer.write("'>");
            writer.write(menu.getName());
            writer.write("</a>");
            if (menu.isSelected()) {
                writeMenu(result, menu.getItems(), writer);
            }
            writer.write("</li>");
        }
        writer.write("</ul>");
    }

    protected void writeResponse(JmxResult result, Writer writer) throws IOException {
        if (result.getResponse() != null) {
            writer.write("<hr/><pre>");
            writer.write(getOutputText());
            writer.write("\n");
            writeValue(result.getResponse(), writer);
            writer.write("</pre><hr/>");
        }
    }

    protected void writeValue(JmxValue value, Writer writer) throws IOException {
        if (value == null) {
            writer.write("null");
        } else if (value instanceof StringJmxValue) {
            writer.write(((StringJmxValue) value).getValue());
        } else if (value instanceof ArrayJmxValue) {
            ArrayJmxValue array = (ArrayJmxValue) value;
            if (array.getName() != null) {
                writer.write("<b>");
                writer.write(array.getName());
                writer.write("</b><br/>");
            }
            writer.write("[");
            JmxValue[] values = array.getValue();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    writer.write(", <br/>");
                }
                writeValue(values[i], writer);
            }
            writer.write("]");
        } else if (value instanceof MapJmxValue) {
            MapJmxValue map = (MapJmxValue) value;
            if (map.getName() != null) {
                writer.write("<b>");
                writer.write(map.getName());
                writer.write("</b><br/>");
            }
            writer.write("<table class='");
            writer.write(getTableClass());
            writer.write("'>");
            for (JmxEntry entry : map.getEntry()) {
                writer.write("<tr><td>");
                writer.write(entry.getKey());
                writer.write("</td><td>");
                writeValue(entry.getValue(), writer);
                writer.write("</td></tr>");
            }
            writer.write("</table>");
        }
    }

    protected void writeOperations(Map<String, String> parameters, JmxResult result, Writer writer)
            throws IOException {
        if (!result.getOperation().isEmpty()) {
            writer.write("<h2>");
            writer.write(getOperationsText());
            writer.write("</h2><form class='");
            writer.write(getFormClass());
            writer.write("' action='");
            writer.write(result.getRequestUri());
            writer.write("'>");
            for (Entry<String, String> parameter : parameters.entrySet()) {
                if (!"operation".equals(parameter.getKey())) {
                    writer.write("<input type='hidden' name='");
                    writer.write(parameter.getKey());
                    writer.write("' value='");
                    writer.write(parameter.getValue());
                    writer.write("'/>");
                }
            }
            writer.write("<select name='operation' class='");
            writer.write(getSelectClass());
            writer.write("'>");
            for (String operation : result.getOperation()) {
                writer.write("<option value='");
                writer.write(operation);
                writer.write("'>");
                writer.write(operation);
                writer.write("</option>");
            }
            writer.write("</select> <button class='");
            writer.write(getButtonCss());
            writer.write("'>");
            writer.write(getButtonSelectText());
            writer.write("</button></form>");
            if (result.getCurrentOperation() != null) {
                writer.write("<h1>");
                writer.write(getOperationText());
                writer.write(" ");
                writer.write(result.getCurrentOperation());
                writer.write("</h1><form class='");
                writer.write(getFormClass());
                writer.write("' method='post' action='");
                writer.write(result.getRequestUri());
                writer.write("?");
                writer.write(result.getQueryParams());
                writer.write("'><input type='hidden' name='op' value='");
                writer.write(result.getCurrentOperation());
                writer.write("'><input type='hidden' name='on' value='");
                writer.write(result.getCurrentObjectName());
                writer.write("'><input type='hidden' name='sign' value='");
                writer.write(result.getCurrentOperationSignature());
                writer.write("'>");
                if (!result.getParameter().isEmpty()) {
                    writer.write("<table class='");
                    writer.write(getTableClass());
                    writer.write("'><thead><tr><th>");
                    writer.write(getNameText());
                    writer.write("</th><th>");
                    writer.write(getValueText());
                    writer.write("</th></tr></thead><tbody>");
                    int nb = 0;
                    for (JmxParameter param : result.getParameter()) {
                        writer.write("<tr><td>");
                        writer.write(param.getName());
                        writer.write("</td><td><input type='text' class='");
                        writer.write(getInputTextClass());
                        writer.write("' name='a");
                        writer.write(String.valueOf(nb));
                        writer.write("' value='");
                        if (param.getValue() != null) {
                            writer.write(param.getValue());
                        }
                        writer.write("'/></td></tr>");
                        nb++;
                    }
                    writer.write("</tbody></table><br/>");
                }
                writer.write("<button class='");
                writer.write(getButtonCss());
                writer.write("'>");
                writer.write(getButtonSendText());
                writer.write("</button></form>");
            }
        }
    }

    protected void writeAttributes(JmxResult result, Writer writer) throws IOException {
        if (!result.getAttribute().isEmpty()) {
            writer.write("<h2>");
            writer.write(getAttributesText());
            writer.write("</h2><table class='");
            writer.write(getTableClass());
            writer.write("'><thead><tr><th>");
            writer.write(getNameText());
            writer.write("</th><th>");
            writer.write(getValueText());
            writer.write("</th></tr></thead><tbody>");
            for (JmxAttribute attr : result.getAttribute()) {
                writer.write("<tr><td>");
                writer.write(attr.getName());
                writer.write("</td><td>");

                if (attr.isWritable()) {
                    writer.write("<form class='");
                    writer.write(getFormClass());
                    writer.write("' method='post' action='");
                    writer.write(result.getRequestUri());
                    writer.write("?");
                    writer.write(result.getQueryParams());
                    writer.write("'><input type='hidden' name='attr' value='");
                    writer.write(attr.getName());
                    writer.write("'><input type='hidden' name='on' value='");
                    writer.write(result.getCurrentObjectName());
                    writer.write("'><input type='hidden' name='type' value='");
                    writer.write(attr.getType());
                    writer.write("'><input type='text' class='");
                    writer.write(getInputTextClass());
                    writer.write("' name='value' value='");
                    writeValue(attr.getValue(), writer);
                    writer.write("'/> <button class='");
                    writer.write(getButtonCss());
                    writer.write("'>");
                    writer.write(getButtonUpdateText());
                    writer.write("</button></form>");
                } else {
                    writeValue(attr.getValue(), writer);
                }
                writer.write("</td></tr>");
            }
            writer.write("</tbody></table>");
        }
    }

}
