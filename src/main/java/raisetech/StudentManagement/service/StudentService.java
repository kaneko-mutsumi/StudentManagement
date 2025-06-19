package raisetech.StudentManagement.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    return studentList.stream()
        .map(student -> {
          StudentDetail studentDetail = new StudentDetail();
          studentDetail.setStudent(student);
          StudentsCourses course = repository.findCourseByStudentId(student.getId());
          studentDetail.setStudentsCourse(course);
          return studentDetail;
        }).toList();
  }

  public StudentDetail getStudentDetailById(int id) {
    Student student = repository.findById(id);
    if (student == null) {
      return null;
    }

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    StudentsCourses course = repository.findCourseByStudentId(id);
    studentDetail.setStudentsCourse(course);
    return studentDetail;
  }

  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentsCourses();
  }

  @Transactional
  public void registerStudentAndCourse(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.insertStudent(student);

    StudentsCourses course = studentDetail.getStudentsCourse();
    course.setStudentId(student.getId());
    repository.insertCourse(course);
  }
}