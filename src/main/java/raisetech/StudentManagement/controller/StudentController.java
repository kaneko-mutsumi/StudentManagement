package raisetech.StudentManagement.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;
import raisetech.StudentManagement.service.StudentService;


@RestController
@RequiredArgsConstructor
public class StudentController {

  private final StudentService service;

  @GetMapping("/studentList")
  public List<Student> getStudentList() {
    return service.searchStudentList();
  }

  @GetMapping("/studentsCoursesList")
  public List<StudentsCourses> getStudentsCoursesList() {
    return service.searchStudentsCoursesList();
  }

  @GetMapping("/students")
  public List<Student> getStudentsByAge(@RequestParam("age") int age){
    return service.searchStudentsByAge(age);
  }

}