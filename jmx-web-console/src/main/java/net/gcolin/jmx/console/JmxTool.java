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

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * Generate the intermediate result.
 *
 * @author Gael COLIN
 *
 */
public class JmxTool {

    /**
     * Build JMX result.
     *
     * @param paramters parameters
     * @return a result
     * @throws JmxProcessException if an error occurs.
     */
    public JmxResult build(Map<String, String> paramters) throws JmxProcessException {
        JmxResult result = new JmxResult();
        processQuery(result, paramters);

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> onames = server.queryNames(null, null);
        Set<String> names = new TreeSet<String>();
        for (ObjectName oname : onames) {
            names.add(oname.getDomain());
        }
        ObjectName content = null;
        String domain = paramters.get("domain");
        for (Object dom : names) {
            JmxMenu menu = new JmxMenu();
            menu.setName(dom.toString());
            menu.setSelected(menu.getName().equals(domain));
            try {
                menu.getParameters().put("domain", URLEncoder.encode(menu.getName(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new JmxProcessException(ex);
            }
            result.getMenu().add(menu);
            if (menu.isSelected()) {
                List<ObjectName> sub = new ArrayList<ObjectName>();
                for (ObjectName oname : onames) {
                    if (menu.getName().equals(oname.getDomain())) {
                        sub.add(oname);
                    }
                }
                List<String> property = new ArrayList<String>();
                String last = null;
                int lastnb = 0;
                List<String> propertyName = new ArrayList<String>();
                for (ObjectName name : sub) {
                    int nb = 0;
                    String dname = name.toString();
                    int start = menu.getName().length() + 1;
                    boolean cont = true;
                    while (cont) {
                        cont = false;
                        int end = dname.indexOf('=', start);
                        if (end != -1) {
                            nb++;
                            String prop = dname.substring(start, end);
                            start = dname.indexOf(',', end);
                            if (start != -1) {
                                cont = true;
                                start++;
                            }
                            String val = paramters.get("$" + prop);
                            if (!propertyName.contains(prop) && name.getKeyProperty(prop).equals(val)) {
                                propertyName.add(prop);
                                property.add(val);
                            } else {
                                if (nb > lastnb) {
                                    lastnb = nb;
                                    last = prop;
                                }
                                if (!name.getKeyProperty(prop).equals(val)) {
                                    cont = false;
                                }
                            }
                        }
                    }
                }
                Map<String, String> propertyMap = new HashMap<String, String>();
                for (int i = 0; i < propertyName.size(); i++) {
                    propertyMap.put(propertyName.get(i), property.get(i));
                }
                for (ObjectName name : sub) {
                    if (name.getKeyPropertyList().equals(propertyMap)) {
                        content = name;
                        break;
                    }
                }

                if (last != null && (propertyName.isEmpty()
                        || !propertyName.get(propertyName.size() - 1).equals(last))) {
                    propertyName.add(last);
                }
                if (!propertyName.isEmpty()) {
                    writeInnerTree(sub, propertyName, property, 0, menu);
                }
            }
        }
        if (content != null) {
            result.setCurrentObjectName(content.toString());
            MBeanInfo info;
            try {
                info = server.getMBeanInfo(content);
                writeAttributes(paramters, server, content, info, result);
                writeOperations(paramters, content, info, result);
            } catch (Exception ex) {
                throw new JmxProcessException(ex);
            }
        }

        return result;
    }

    protected void writeOperations(Map<String, String> paramters, ObjectName content, MBeanInfo info,
            JmxResult result) {
        MBeanOperationInfo[] operations = info.getOperations();
        if (operations.length > 0) {
            String opParam = paramters.get("operation");
            MBeanOperationInfo currentOp = null;
            for (MBeanOperationInfo operation : operations) {
                result.getOperation().add(operation.getName());
                if (operation.getName().equals(opParam)) {
                    currentOp = operation;
                    result.setCurrentOperation(operation.getName());
                }
            }
            if (currentOp != null) {
                StringBuilder signature = new StringBuilder("[");
                for (MBeanParameterInfo param : currentOp.getSignature()) {
                    if (signature.length() > 1) {
                        signature.append(", ");
                    }
                    signature.append(param.getType());
                }
                signature.append("]");
                result.setCurrentOperationSignature(signature.toString());

                if (currentOp.getSignature().length > 0) {
                    int nb = 0;
                    for (MBeanParameterInfo param : currentOp.getSignature()) {
                        JmxParameter parameter = new JmxParameter();
                        parameter.setName(param.getName());
                        parameter.setInternalName("a" + nb);
                        parameter.setValue(paramters.get(parameter.getInternalName()));
                        result.getParameter().add(parameter);
                        nb++;
                    }
                }
            }
        }
    }

    protected void writeAttributes(Map<String, String> paramters, MBeanServer server,
            ObjectName content, MBeanInfo info, JmxResult result) {
        MBeanAttributeInfo[] attributes = info.getAttributes();
        if (attributes.length > 0) {
            for (MBeanAttributeInfo attr : attributes) {
                JmxAttribute attribute = new JmxAttribute();
                attribute.setName(attr.getName());
                attribute.setWritable(attr.isWritable());
                attribute.setType(attr.getType());
                result.getAttribute().add(attribute);
                if (attr.isReadable()) {
                    try {
                        attribute.setValue(format(server.getAttribute(content, attr.getName())));
                    } catch (Exception ex) {
                        attribute.setValue(new StringJmxValue("Error: " + ex.getMessage()));
                    }
                }
            }
        }
    }

    protected void writeInnerTree(List<ObjectName> it, List<String> propertyName,
            List<String> property, int index, JmxMenu menu) throws JmxProcessException {
        String propName = propertyName.get(index);
        String propValue = property.size() == index ? null : property.get(index);
        Map<String, List<ObjectName>> filtered = new TreeMap<String, List<ObjectName>>();
        for (ObjectName name : it) {
            if (name.getKeyPropertyList().containsKey(propName)) {
                List<ObjectName> list = filtered.get(name.getKeyProperty(propName));
                if (list == null) {
                    list = new ArrayList<ObjectName>();
                    filtered.put(name.getKeyProperty(propName), list);
                }
                list.add(name);
            }
        }
        for (Entry<String, List<ObjectName>> entry : filtered.entrySet()) {
            JmxMenu item = new JmxMenu();
            boolean selected = entry.getKey().equals(propValue);
            item.setSelected(selected);
            item.setName(entry.getKey());
            item.getParameters().putAll(menu.getParameters());
            try {
                item.getParameters().put(propName.equals("domain") ? propName : "$" + propName,
                        URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new JmxProcessException(ex);
            }
            menu.getItems().add(item);
            if (selected && propertyName.size() > index + 1) {
                writeInnerTree(entry.getValue(), propertyName, property, index + 1, item);
            }
        }
    }

    protected void processQuery(JmxResult result, Map<String, String> paramters)
            throws JmxProcessException {
        String op = paramters.get("op");
        String attr = paramters.get("attr");
        if (op != null) {
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName oname
                        = server.queryNames(new ObjectName(paramters.get("on")), null).iterator().next();
                String sign = paramters.get("sign");
                sign = sign.substring(1, sign.length() - 1);
                String[] signature = sign.isEmpty() ? new String[0] : sign.split(",");
                Object[] params = new Object[signature.length];
                for (int i = 0; i < signature.length; i++) {
                    signature[i] = signature[i].trim();
                    params[i] = parse(signature[i], paramters.get("a" + i));
                }
                Object response = server.invoke(oname, op, params, signature);
                result.setResponse(response == null ? new StringJmxValue("Operation executed successfully")
                        : format(response));
            } catch (Exception ex) {
                throw new JmxProcessException(ex);
            }
        } else if (attr != null) {
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName oname
                        = server.queryNames(new ObjectName(paramters.get("on")), null).iterator().next();
                server.setAttribute(oname,
                        new Attribute(attr, parse(paramters.get("type"), paramters.get("value"))));
                result.setResponse(new StringJmxValue("Attribute " + attr + " updated"));
            } catch (Exception ex) {
                throw new JmxProcessException(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected JmxValue format(Object obj) {
        if (obj != null && obj.getClass().isArray()) {
            JmxValue[] values = new JmxValue[Array.getLength(obj)];
            for (int i = 0; i < values.length; i++) {
                values[i] = format(Array.get(obj, i));
            }
            return new ArrayJmxValue(values);
        } else if (obj != null && obj instanceof CompositeData) {
            CompositeData data = (CompositeData) obj;
            List<JmxEntry> entries = new ArrayList<JmxEntry>();
            for (String key : data.getCompositeType().keySet()) {
                entries.add(new JmxEntry(key, format(data.get(key))));
            }
            return new MapJmxValue(data.getCompositeType().getTypeName(), entries);
        } else if (obj != null && obj instanceof TabularData) {
            TabularData data = (TabularData) obj;
            JmxValue[] values = new JmxValue[data.size()];
            int idx = 0;
            for (Object keys : data.keySet()) {
                values[idx++] = format(data.get(((List<?>) keys).toArray()));
            }
            return new ArrayJmxValue(data.getTabularType().getTypeName(), values);
        } else if (obj != null && obj instanceof Map) {
            Map<Object, Object> data = (Map<Object, Object>) obj;
            List<JmxEntry> entries = new ArrayList<JmxEntry>();
            for (Entry<Object, Object> entry : data.entrySet()) {
                entries.add(new JmxEntry(String.valueOf(entry.getKey()), format(entry.getValue())));
            }
            return new MapJmxValue(entries);
        } else if (obj != null && obj instanceof Collection) {
            return format(((Collection<?>) obj).toArray());
        } else {
            return new StringJmxValue(String.valueOf(obj));
        }
    }

    protected Object parse(String type, String parameter) throws JmxProcessException {
        if ("null".equals(parameter)) {
            return null;
        }
        if ("java.lang.String".equals(type)) {
            return parameter;
        }
        if ("java.lang.Integer".equals(type) || "int".equals(type)) {
            return Integer.parseInt(parameter);
        }
        if ("java.lang.Long".equals(type) || "long".equals(type)) {
            return Long.parseLong(parameter);
        }
        if ("java.lang.Double".equals(type) || "double".equals(type)) {
            return Double.parseDouble(parameter);
        }
        if ("java.lang.Float".equals(type) || "float".equals(type)) {
            return Float.parseFloat(parameter);
        }
        if ("java.lang.Short".equals(type) || "short".equals(type)) {
            return Short.parseShort(parameter);
        }
        if ("java.lang.Byte".equals(type) || "byte".equals(type)) {
            return Byte.parseByte(parameter);
        }
        if ("java.lang.Character".equals(type) || "char".equals(type)) {
            return parameter.charAt(0);
        }
        if ("java.lang.Boolean".equals(type) || "boolean".equals(type)) {
            return Boolean.parseBoolean(parameter);
        }
        throw new JmxProcessException("unsupported type " + type);
    }

}
