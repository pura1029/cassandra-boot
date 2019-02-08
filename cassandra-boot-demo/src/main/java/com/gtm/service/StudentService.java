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

package com.gtm.service;

import java.util.List;
import java.util.Optional;

import com.datastax.driver.core.PagingState;
import com.gtm.model.Student;
import com.gtm.repository.StudentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

/**
 * Insert your comment for StudentService here
 *
 * @author kumargautam
 */
@Service
@Log4j2
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;


    public Student save(Student student) {
        log.info("Start execution of save() method");
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        log.info("Start execution of getAllStudents() method");
        return studentRepository.findAll();
    }

    public List<Student> getAllStudentsByName(String studentName) {
        log.info("Start execution of getAllStudentsByName() method");
        return studentRepository.findByName(studentName);
    }

    public Student getStudentById(String id) {
        log.info("Start execution of getStudentById() method");
        Optional<Student> optional = studentRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public void deleteStudentsByName(List<String> studentNames) {
        log.info("Start execution of deleteStudentsByName() method");
        studentRepository.deleteByNameIn(studentNames);
    }

    public Slice<Student> getStudentsBasedOnPage(int page, int size) {
        log.info("Start execution of getStudentsBasedOnPage() method");
        if (page == 1) {
            return studentRepository.findAll(CassandraPageRequest.of(page - 1, size));
        } else {
            CassandraPageRequest cassandraPageRequest = CassandraPageRequest.of(0, size);
            Slice<Student> slice = studentRepository.findAll(cassandraPageRequest);
            for (int i = 1; i < page; i++) {
                PagingState pagingState = ((CassandraPageRequest) slice.getPageable()).getPagingState();
                if (pagingState == null) {
                    return slice;
                }
                cassandraPageRequest = CassandraPageRequest.of(slice.getPageable(), pagingState);
                slice = studentRepository.findAll(cassandraPageRequest);
            }
            return slice;
        }
    }

    public int getTotalPages(Slice<Student> slice) {
        long totalCount = studentRepository.count();
        return slice.getSize() == 0 ? 1 : (int) Math.ceil((double) totalCount / (double) slice.getSize());
    }
}