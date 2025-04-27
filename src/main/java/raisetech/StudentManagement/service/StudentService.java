package raisetech.StudentManagement.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;
import raisetech.StudentManagement.repository.StudentRepository;

@RequiredArgsConstructor
@Service
public class StudentService {

  private final StudentRepository repository;

  // 年代で絞り込む
  public List<Student> searchStudentsByAge(int age) {
    int from = age;
    int to = age + 9;

    return repository.findStudentsByAgeRange(from, to);
  }

  // コース名で絞り込む
  public List<StudentsCourses> searchCoursesByCourseName(String courseName) {
    return repository.findCoursesByName(courseName);
  }

  // コース名で絞り込む
  public List<StudentsCourses> searchCoursesByCourseName(String courseName) {
    return repository.searchStudentsCourses().stream()
        .filter(course -> courseName.equals(course.getCourseName()))
        .collect(Collectors.toList());
  }
}
