package raisetech.StudentManagement.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {

    this.repository = repository;
  }

  public List<Student> searchStudentList() {

    return repository.search();
  }

  public List<StudentsCourses> searchStudentsCourseList() {

    return repository.searchStudentsCourses();

  }

  public void insertStudent(Student student) {
    repository.insertStudent(student); //学生情報DB登録を依頼
  }

  public void insertCourse(StudentsCourses course, String studentId){
    course.setStudentId(Integer.parseInt(studentId));
    repository.insertCourse(course);//コース情報DB登録を依頼
  }


}
