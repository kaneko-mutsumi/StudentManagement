package raisetech.StudentManagement.service;


import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.date.Student;
import raisetech.StudentManagement.date.StudentsCourses;
import raisetech.StudentManagement.filter.StudentFilter;
import raisetech.StudentManagement.repository.StudentRepository;

@RequiredArgsConstructor
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentFilter filter;

  public List<Student> searchStudentList() {
    return repository.search().stream()
        .filter(filter::isIn30s)
        .collect(Collectors.toList());
  }

  public List<StudentsCourses> searchStudentsCoursesList() {
    return repository.searchStudentsCourses().stream()
        .filter(filter::isJavaCourse)
        .collect(Collectors.toList());
  }
}
