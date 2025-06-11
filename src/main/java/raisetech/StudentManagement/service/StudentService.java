package raisetech.StudentManagement.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.repository.StudentRepository;
import raisetech.StudentManagement.domain.StudentDetail;


@Service
public class StudentService {
  private StudentRepository repository;
  private static final Logger log = LoggerFactory.getLogger(StudentService.class);



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

  // 学生とコースを同時登録
  @Transactional
  public void registerStudentAndCourse(StudentDetail studentDetail) {
    try {
      Student student = studentDetail.getStudent();
      StudentsCourses course = studentDetail.getStudentsCourse();

      // 学生登録（ID自動採番）
      repository.insertStudent(student);
      log.info("学生を登録しました: {}", student.getId());

      // 学生IDをコースにセット
      course.setStudentId(student.getId());

      if (course.getEndDate() == null && course.getStartDate() != null) {
        course.setEndDate(course.getStartDate().plus(1, ChronoUnit.YEARS));
      }

      repository.insertCourse(course);
      log.info("コースを登録しました（受講生ID: {}）", course.getStudentId());

    } catch (Exception e) {
      log.error("学生とコースの登録中にエラーが発生しました", e);
      throw new RuntimeException("登録に失敗しました。やり直してください。", e);
    }
  }
}
