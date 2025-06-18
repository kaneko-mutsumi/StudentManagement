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

  // 学生一覧を検索
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    return studentList.stream()
        .map(student -> {
          StudentDetail studentDetail = new StudentDetail();
          studentDetail.setStudent(student);

          // 学生に対応するコース情報を取得
          StudentsCourses course = repository.findCourseByStudentId(student.getId());
          studentDetail.setStudentsCourse(course);

          return studentDetail;
        }).toList();
  }

  // IDで学生詳細を検索
  public StudentDetail getStudentDetailById(int id) {
    Student student = repository.findById(id);
    if (student == null) {
      return null;
    }

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);

    // 学生に対応するコース情報を取得
    StudentsCourses course = repository.findCourseByStudentId(id);
    studentDetail.setStudentsCourse(course);

    return studentDetail;
  }

  // 学生情報を更新（コース情報の新規作成に対応）
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // 学生情報を更新
    if (studentDetail.getStudent() != null) {
      repository.updateStudent(studentDetail.getStudent());
    }

    // コース情報の処理
    if (studentDetail.getStudentsCourse() != null) {
      StudentsCourses course = studentDetail.getStudentsCourse();

      // 🆕 コースIDが0または null の場合は新規作成
      if (course.getId() == 0 || course.getId() == null) {
        System.out.println("コース情報を新規作成します: 学生ID = " + course.getStudentId());
        repository.insertCourse(course);
      } else {
        System.out.println("コース情報を更新します: コースID = " + course.getId());
        repository.updateCourse(course);
      }
    }
  }

  // コース一覧を検索
  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentsCourses();
  }

  // 学生とコースを新規登録
  @Transactional
  public void registerStudentAndCourse(StudentDetail studentDetail) {
    // 学生情報を登録
    Student student = studentDetail.getStudent();
    repository.insertStudent(student);

    // 登録された学生のIDをコース情報に設定
    StudentsCourses course = studentDetail.getStudentsCourse();
    course.setStudentId(student.getId());
    repository.insertCourse(course);
  }
}