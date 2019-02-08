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

import com.datastax.driver.mapping.annotations.Column;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Insert your comment for DiskTask here
 *
 * @author kumargautam
 */
@Table("task")
@Data
public class DiskTask implements Task {

    private String taskDesc;
    private Integer spaceToFull;
    private String taskId;
    private String taskName;

    @Override
    @PrimaryKey
    public String getTaskId() {
        return taskId;
    }

    @Override
    @Column
    public String getTaskName() {
        return taskName;
    }

}
