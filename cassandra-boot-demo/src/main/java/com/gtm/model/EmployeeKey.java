/*
 * Copyright (c) 2018 All Rights Reserved.
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

package com.gtm.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

/**
 * Insert your comment for EmployeeKey here
 *
 * @author kumargautam
 */
@PrimaryKeyClass
@Data
public class EmployeeKey implements Serializable {

    private static final long serialVersionUID = 1L;
    @PrimaryKeyColumn(name = "empId", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String empId;
    @PrimaryKeyColumn(name = "empName", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String empName;

    public EmployeeKey() {
        this.empId = UUID.randomUUID().toString();
    }
}
