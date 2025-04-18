package raisetech.StudentManagement.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;
import raisetech.StudentManagement.service.StudentService;

@RestController
@RequiredArgsConstructor
public class StudentController {

  private final StudentService service;

  // 年代で絞り込む（例：http://localhost:8080/students?age=30）
  @GetMapping("/students")
  public List<Student> getStudentsByAge(@RequestParam("age") int age){
    return service.searchStudentsByAge(age);
  }

  // コース名で受講生を絞り込む（例：http://localhost:8080/courses?course=Javaコース）
  @GetMapping("/courses")
  public List<StudentsCourses> getStudentsCoursesByCourseName(@RequestParam("course") String courseName){
    return service.searchCoursesByCourseName(courseName);
  }
}
