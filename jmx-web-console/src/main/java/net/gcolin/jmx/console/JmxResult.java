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

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate result. Contains all data for building the final page.
 *
 * @author Gael COLIN
 *
 */
public class JmxResult {

    private List<JmxMenu> menu = new ArrayList<JmxMenu>();
    private List<JmxAttribute> attribute = new ArrayList<JmxAttribute>();
    private List<String> operation = new ArrayList<String>();
    private String currentOperation;
    private String currentObjectName;
    private String currentOperationSignature;
    private List<JmxParameter> parameter = new ArrayList<JmxParameter>();
    private JmxValue response;
    private String requestUri;
    private String queryParams;

    public List<JmxMenu> getMenu() {
        return menu;
    }

    public void setMenu(List<JmxMenu> menu) {
        this.menu = menu;
    }

    public List<JmxAttribute> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<JmxAttribute> attribute) {
        this.attribute = attribute;
    }

    public List<String> getOperation() {
        return operation;
    }

    public void setOperation(List<String> operation) {
        this.operation = operation;
    }

    public List<JmxParameter> getParameter() {
        return parameter;
    }

    public void setParameter(List<JmxParameter> parameter) {
        this.parameter = parameter;
    }

    public JmxValue getResponse() {
        return response;
    }

    public void setResponse(JmxValue response) {
        this.response = response;
    }

    public String getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(String currentOperation) {
        this.currentOperation = currentOperation;
    }

    public String getCurrentObjectName() {
        return currentObjectName;
    }

    public void setCurrentObjectName(String currentObjectName) {
        this.currentObjectName = currentObjectName;
    }

    public String getCurrentOperationSignature() {
        return currentOperationSignature;
    }

    public void setCurrentOperationSignature(String currentOperationSignature) {
        this.currentOperationSignature = currentOperationSignature;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

}
