/*
 * Copyright 2015-2017 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.commons;

import java.util.Map;

/**
 * Created by imedina on 03/07/16.
 */
public class OntologyTerm {

    protected String id;
    protected String name;
    protected String source;

    protected Map<String, String> attributes;

    public OntologyTerm() {
    }

    public OntologyTerm(String id, String name, String source, Map<String, String> attributes) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OntologyTerm{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public OntologyTerm setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OntologyTerm setName(String name) {
        this.name = name;
        return this;
    }

    public String getSource() {
        return source;
    }

    public OntologyTerm setSource(String source) {
        this.source = source;
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public OntologyTerm setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }
}
