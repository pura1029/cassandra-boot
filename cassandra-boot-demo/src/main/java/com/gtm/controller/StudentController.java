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

package com.gtm.controller;

import java.io.IOException;
import java.util.List;

import com.gtm.model.Student;
import com.gtm.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Insert your comment for StudentController here
 *
 * @author kumargautam
 */
@RestController
@RequestMapping("/rest/api/v1/students")
@Api("/rest/api/v1/students")
@Log4j2
public class StudentController {
    @Autowired
    private StudentService studentService;

    @ApiOperation(value = "API to add a new student", nickname = "addStudent")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        log.info("Start execution of addStudent() method");
        Student student2 = studentService.save(student);
        return new ResponseEntity<>(student2, HttpStatus.OK);
    }

    @ApiOperation(value = "API to add a student photo", nickname = "uploadStudentPhoto")
    @PostMapping(value = "/photo", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Student> uploadStudentPhoto(@RequestParam(name = "id") String id,
            @RequestParam(name = "stuPhoto", required = false) MultipartFile stuPhoto) throws IOException {
        log.info("Start execution of uploadStudentPhoto() method");
        Student student = studentService.getStudentById(id);
        student.setStuPhoto(stuPhoto.getBytes());//ByteBuffer.wrap(stuPhoto.getBytes())
        Student student2 = studentService.save(student);
        return new ResponseEntity<>(student2, HttpStatus.OK);
    }

    @ApiOperation(value = "API to get all the students", nickname = "getStudents")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Student>> getStudents() {
        log.info("Start execution of getStudents() method");
        List<Student> studentList = studentService.getAllStudents();
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @ApiOperation(value = "API to get all the students by name", nickname = "getStudentByName")
    @GetMapping(value = "/{studentName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Student>> getStudentByName(@PathVariable("studentName") String studentName) {
        log.info("Start execution of getStudentByName() method");
        List<Student> studentList = studentService.getAllStudentsByName(studentName);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @ApiOperation(value = "API to delete an students by name", nickname = "deleteStudentsByName")
    @DeleteMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> deleteStudents(@RequestParam List<String> studentNames) {
        log.info("Start execution of deleteStudents() method");
        studentService.deleteStudentsByName(studentNames);
        return new ResponseEntity<>(studentNames, HttpStatus.OK);
    }

    @ApiOperation(value = "API to get all the students based on page", nickname = "getStudentsBasedOnPage")
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Student>> getStudentsBasedOnPage(
            @RequestParam(value = "pageNo", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "4") int size) {
        log.info("Start execution of getStudentsBasedOnPage() method");
        Slice<Student> result = studentService.getStudentsBasedOnPage(page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.add("totalPage", String.valueOf(studentService.getTotalPages(result)));
        return new ResponseEntity<>(result.getContent(), headers, HttpStatus.OK);
    }
}