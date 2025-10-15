package raisetech.StudentManagement.controller.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import raisetech.StudentManagement.exception.ResourceNotFoundException;
import raisetech.StudentManagement.exception.TestException;

/**
 * アプリケーション全体の共通例外ハンドラー
 * 全てのControllerで発生した例外をここで一元的に処理する
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * TestException専用グローバルハンドラー
   * アプリケーション全体でTestExceptionが発生した場合の共通処理
   *
   * @param e TestException
   * @return エラーレスポンス
   */
  @ExceptionHandler(TestException.class)
  public ResponseEntity<GlobalErrorResponse> handleTestException(TestException e) {
    logger.warn("【グローバル】TestException発生: {}", e.getMessage());

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "システムエラー",
        e.getMessage(),
        "TestException"
    );

    // 400 Bad Request として返す
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * ResourceNotFoundException専用ハンドラー
   * リソース（学生など）が見つからない場合の処理
   *
   * @param e ResourceNotFoundException
   * @return エラーレスポンス（404）
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException e) {
    logger.warn("【グローバル】リソースが見つかりません: {}", e.getMessage());

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "リソースが見つかりません",
        e.getMessage(),
        "ResourceNotFoundException"
    );

    // 404 Not Found として返す
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * MethodArgumentNotValidException専用ハンドラー
   * @Valid によるバリデーション失敗時の処理
   *
   * @param e MethodArgumentNotValidException
   * @return エラーレスポンス（400）
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GlobalErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {

    // バリデーションエラーの詳細を取得
    String errorDetails = e.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((a, b) -> a + ", " + b)
        .orElse("入力内容に誤りがあります");

    logger.warn("【グローバル】バリデーションエラー: {}", errorDetails);

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "入力内容に誤りがあります",
        errorDetails,
        "MethodArgumentNotValidException"
    );

    // 400 Bad Request として返す
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * MethodArgumentTypeMismatchException専用ハンドラー
   * URLパラメータの型変換に失敗した場合の処理
   * 例: /api/students/abc → IDが数字ではない
   *
   * @param e MethodArgumentTypeMismatchException
   * @return エラーレスポンス（400）
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<GlobalErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    logger.warn("【グローバル】型変換エラー: {}", e.getMessage());

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "入力形式が正しくありません",
        e.getName() + "は数字で指定してください",
        "MethodArgumentTypeMismatchException"
    );

    // 400 Bad Request として返す
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * IllegalArgumentException専用ハンドラー
   * 不正な引数エラーの処理
   *
   * @param e IllegalArgumentException
   * @return エラーレスポンス
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GlobalErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    logger.warn("【グローバル】不正な引数エラー: {}", e.getMessage());

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "入力内容に問題があります",
        e.getMessage(),
        "IllegalArgumentException"
    );

    // 400 Bad Request として返す
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * その他の予期しない例外の共通処理
   * RuntimeExceptionやその他の例外をキャッチする
   *
   * @param e Exception
   * @return エラーレスポンス
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<GlobalErrorResponse> handleGeneralException(Exception e) {
    logger.error("【グローバル】予期しない例外発生: {}", e.getMessage(), e);

    GlobalErrorResponse response = new GlobalErrorResponse(
        "error",
        "内部サーバーエラーが発生しました",
        "システム管理者にお問い合わせください",
        e.getClass().getSimpleName()
    );

    // 500 Internal Server Error として返す
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  /**
   * グローバルエラーレスポンス用クラス
   * 詳細な情報を含むエラーレスポンス
   */
  public static class GlobalErrorResponse {
    private String status;
    private String message;
    private String detail;
    private String exceptionType;

    public GlobalErrorResponse(String status, String message, String detail, String exceptionType) {
      this.status = status;
      this.message = message;
      this.detail = detail;
      this.exceptionType = exceptionType;
    }

    // Getter methods
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getDetail() { return detail; }
    public String getExceptionType() { return exceptionType; }
  }
}