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
package org.apache.isis.core.commons.binding;

public interface Bindable<T> {

    Object getBean();

    String getName();

    T getValue();

    void setValue(T newValue);

    boolean isBound();

    void bind(Bindable<T> observable);
    void unbind();
    
    void bindBidirectional(Bindable<T> other);
    void unbindBidirectional(Bindable<T> other);

    void addListener(ChangeListener<? super T> listener);

    void removeListener(ChangeListener<? super T> listener);
   

}
