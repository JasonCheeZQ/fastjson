/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Member;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
public abstract class FieldSerializer {

    public final FieldInfo fieldInfo;
    private final String   double_quoted_fieldPrefix;
    private final String   single_quoted_fieldPrefix;
    private final String   un_quoted_fieldPrefix;
    protected boolean      writeNull = false;

    public FieldSerializer(FieldInfo fieldInfo){
        super();
        this.fieldInfo = fieldInfo;
        fieldInfo.setAccessible(true);

        this.double_quoted_fieldPrefix = '"' + fieldInfo.name + "\":";

        this.single_quoted_fieldPrefix = '\'' + fieldInfo.name + "\':";

        this.un_quoted_fieldPrefix = fieldInfo.name + ":";

        JSONField annotation = fieldInfo.getAnnotation();
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteMapNullValue) {
                    writeNull = true;
                }
            }
        }
    }

    public void writePrefix(JSONSerializer serializer) throws IOException {
        SerializeWriter out = serializer.out;

        if (out.isEnabled(SerializerFeature.QuoteFieldNames)) {
            if (out.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.write(single_quoted_fieldPrefix);
            } else {
                out.write(double_quoted_fieldPrefix);
            }
        } else {
            out.write(un_quoted_fieldPrefix);
        }
    }

    public Object getPropertyValue(Object object) throws Exception {
        try {
            return fieldInfo.get(object);
        } catch (Exception ex) {
            
            Member member;
            
            if (fieldInfo.method != null) {
                member = fieldInfo.method;
            } else {
                member = fieldInfo.field;
            }
            
            String qualifiedName = member.getDeclaringClass().getName() + "." + member.getName();
            
            throw new JSONException("get property error。 " + qualifiedName, ex);
        }
    }

    public abstract void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception;

    public abstract void writeValue(JSONSerializer serializer, Object propertyValue) throws Exception;
}
