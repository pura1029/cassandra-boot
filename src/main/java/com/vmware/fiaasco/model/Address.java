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

import com.datastax.driver.core.DataType.Name;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

/**
 * Insert your comment for Address here
 *
 * @author kumargautam
 */
@UserDefinedType("address")
@Data
public class Address {
    @CassandraType(type = Name.VARCHAR)
    private String street;

    private String city;

    private String zipcode;
    
    @CassandraType(type = Name.LIST, typeArguments = Name.BIGINT)
    private List<Long> timestamps;
    
}
