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

package com.vmware.fiaasco.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.vmware.fiaasco.model.Student;

/**
 * Insert your comment for StudentRepository here
 *
 * @author kumargautam
 */
@Repository
public interface StudentRepository extends CassandraRepository<Student, String> {

    @SuppressWarnings("unchecked")
    @Override
    Student save(Student student);

    @Override
    @AllowFiltering
    Optional<Student> findById(String id);

    @Query(value = "SELECT id ,name , address FROM student WHERE id IN ?0", allowFiltering = true)
    List<Student> findByIdIn(Collection<String> ids);

    @Override
    List<Student> findAll();

    @Override
    Slice<Student> findAll(Pageable pageable);

    @Override
    void deleteById(String id);

    @Query("SELECT * FROM Student WHERE name = ?0 ALLOW FILTERING")
    List<Student> findByName(String name);

    @Query("DELETE from Student WHERE name IN ?0")
    void deleteByNameIn(Collection<String> names);
}
