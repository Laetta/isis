/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.applib.layout.menubars;

import java.util.Map;

import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.layout.component.ServiceActionLayoutData;

/**
 * @since 1.x {@index}
 */
public interface MenuBars {

    @FunctionalInterface
    interface Visitor {
        void visit(final ServiceActionLayoutData serviceActionLayoutData);
    }

    String getTnsAndSchemaLocation();

    void setTnsAndSchemaLocation(final String tnsAndSchemaLocation);

    MenuBar menuBarFor(DomainServiceLayout.MenuBar menuBar);

    void visit(Visitor visitor);

    Map<String, ServiceActionLayoutData> getAllServiceActionsByObjectTypeAndId();

}
