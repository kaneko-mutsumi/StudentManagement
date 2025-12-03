package raisetech.StudentManagement.response;

import java.util.List;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 * 学生一覧取得APIのレスポンスDTO
 *
 * <p>REST APIのレスポンス形式を統一し、API設計を明確にします。</p>
 */
public class StudentListResponse {

  private String status;
  private int count;
  private List<StudentDetail> students;

  public StudentListResponse(String status, int count, List<StudentDetail> students) {
    this.status = status;
    this.count = count;
    this.students = students;
  }

  // Getter methods
  public String getStatus() {
    return status;
  }

  public int getCount() {
    return count;
  }

  public List<StudentDetail> getStudents() {
    return students;
  }
}