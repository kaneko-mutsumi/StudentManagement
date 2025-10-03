package raisetech.StudentManagement.exception;

/**
 * リソースが見つからない場合の例外
 * この例外が発生した場合、HTTPステータス404を返す
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * メッセージ付きコンストラクタ
   * @param message エラーメッセージ（例: "学生が見つかりません: ID=999"）
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}