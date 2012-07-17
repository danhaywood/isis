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

package org.apache.isis.tck.dom.scalars;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Discriminator("WRPV")
@javax.jdo.annotations.Query(
        name="wrpv_findByStringProperty", language="JDOQL",  
        value="SELECT FROM org.apache.isis.tck.dom.scalars.WrapperValuedEntity WHERE stringProperty == :i")
@javax.persistence.Entity                    
@javax.persistence.DiscriminatorValue("WRPV")
@ObjectType("WRPV")
public class WrapperValuedEntity extends AbstractDomainObject {

    // {{ StringProperty (also pk, title)
    @javax.jdo.annotations.PrimaryKey
    @javax.persistence.Id
    private String stringProperty;

    @Title
    @Optional
    @MemberOrder(sequence = "1")
    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(final String description) {
        this.stringProperty = description;
    }

    // }}

    // {{ BooleanProperty
    private Boolean booleanProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Boolean getBooleanProperty() {
        return booleanProperty;
    }

    public void setBooleanProperty(final Boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    // }}

    // {{ ByteProperty
    private Byte byteProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Byte getByteProperty() {
        return byteProperty;
    }

    public void setByteProperty(final Byte byteWProperty) {
        this.byteProperty = byteWProperty;
    }

    // }}

    // {{ ShortProperty
    private Short shortProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Short getShortProperty() {
        return shortProperty;
    }

    public void setShortProperty(final Short shortProperty) {
        this.shortProperty = shortProperty;
    }

    // }}

    // {{ IntegerProperty
    private Integer integerProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Integer getIntegerProperty() {
        return integerProperty;
    }

    public void setIntegerProperty(final Integer integerProperty) {
        this.integerProperty = integerProperty;
    }

    // }}

    // {{ LongProperty
    private Long longProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Long getLongProperty() {
        return longProperty;
    }

    public void setLongProperty(final Long longProperty) {
        this.longProperty = longProperty;
    }

    // }}

    // {{ FloatProperty
    private Float floatProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Float getFloatProperty() {
        return floatProperty;
    }

    public void setFloatProperty(final Float floatWProperty) {
        this.floatProperty = floatWProperty;
    }

    // }}

    // {{ DoubleProperty
    private Double doubleProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Double getDoubleProperty() {
        return doubleProperty;
    }

    public void setDoubleProperty(final Double doubleWProperty) {
        this.doubleProperty = doubleWProperty;
    }
    // }}

    // {{ CharacterProperty (property)
    @javax.jdo.annotations.Column(jdbcType="char", length=1) // works for hsqldb
    //@javax.jdo.annotations.Column(jdbcType="char", length=1, sqlType="char") // works for mssqlserver
    private Character characterProperty;

    @Optional
    @MemberOrder(sequence = "1")
    public Character getCharacterProperty() {
        return characterProperty;
    }

    public void setCharacterProperty(final Character charProperty) {
        this.characterProperty = charProperty;
    }
    // }}


}
