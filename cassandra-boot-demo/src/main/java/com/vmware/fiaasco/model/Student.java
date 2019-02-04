/*
 * Copyright (c) 2018 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.fiaasco.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.datastax.driver.core.DataType.Name;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * Insert your comment for Student here
 *
 * @author kumargautam
 */
@Table(value = "Student")
@Data
public class Student {

    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    //@PrimaryKey(value = "id")
    private String id;

    @PrimaryKeyColumn(name = "name", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    //@Column
    //@Indexed
    @CassandraType(type = Name.VARCHAR)
    private String name;
    @Column
    @CassandraType(type = Name.INT)
    private Integer age;
    @Column
    @CassandraType(type = Name.INT)
    private Integer marks;
    @Column
   // @CassandraType(type = Name.UDT, userTypeName = "address")
    private Address address;

    @Column
    @CassandraType(type = Name.VARCHAR)
    private Gender gender;

    @Column
    @CassandraType(type = Name.LIST, typeArguments = Name.UDT, userTypeName = "address")
    private List<Address> listOfAddress;

    @Column
    private Map<String, Address> mapOfAddress;

    /*@Column
    @CassandraType(type = Name.LIST, typeArguments = Name.LIST)
    private List<List<String>> grades;*/

    @Column
    @JsonIgnore
    @CassandraType(type = Name.BLOB)
    private byte[] stuPhoto; //ByteBuffer or byte[]


    /**
     * // UUIDs.timeBased().toString();
     * 
     * //UUID.randomUUID().toString();
     */
    public Student() {
        this.id = UUID.randomUUID().toString();
    }
}