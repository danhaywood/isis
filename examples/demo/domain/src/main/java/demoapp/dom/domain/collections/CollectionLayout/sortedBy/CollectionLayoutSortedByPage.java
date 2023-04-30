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
package demoapp.dom.domain.collections.CollectionLayout.sortedBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Named;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.CollectionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.util.ObjectContracts;

import demoapp.dom.domain.collections.CollectionLayout.sequence.child.CollectionLayoutSequenceChildVm;

import demoapp.dom.domain.collections.CollectionLayout.sortedBy.child.CollectionLayoutSortedByChildVm;

import lombok.Getter;
import lombok.Setter;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;

//tag::class[]
@Named("demo.CollectionLayoutSortedByPage")
@DomainObject(nature=Nature.VIEW_MODEL)
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionLayoutSortedByPage implements HasAsciiDocDescription {

    @ObjectSupport public String title() {
        return "@CollectionLayout#sortedBy";
    }

//tag::comparator[]
    public static class ValueComparator implements Comparator<CollectionLayoutSequenceChildVm> {
        @Override
        public int compare(CollectionLayoutSequenceChildVm o1, CollectionLayoutSequenceChildVm o2) {
            return ObjectContracts.contract(CollectionLayoutSequenceChildVm.class)  // <.>
                    .thenUse("value", CollectionLayoutSequenceChildVm::getValue)    // <.>
                    .compare(o1, o2);
        }
    }
//end::comparator[]


//tag::children[]
    @Collection()
    @CollectionLayout(
            sortedBy = ValueComparator.class        // <.>
    )
    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    @Getter @Setter
    private List<CollectionLayoutSortedByChildVm> children = new ArrayList<>();
//end::children[]

}
//end::class[]
