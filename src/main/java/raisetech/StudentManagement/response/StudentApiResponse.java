package raisetech.StudentManagement.response;

/**
 * API レスポンス用クラス
 * 学生関連APIの標準レスポンス形式
 */
public class StudentApiResponse {

  private String status;
  private String message;
  private String data;

  public StudentApiResponse(String status, String message, String data) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  // Getter methods
  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public String getData() {
    return data;
  }
}