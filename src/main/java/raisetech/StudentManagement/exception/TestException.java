package raisetech.StudentManagement.exception;

/**
 * テスト用カスタム例外クラス
 * RuntimeExceptionを継承することで非チェック例外として扱える
 */
public class TestException extends RuntimeException {

  /**
   * デフォルトコンストラクタ
   */
  public TestException() {
    super();
  }

  /**
   * メッセージ付きコンストラクタ
   * @param message エラーメッセージ
   */
  public TestException(String message) {
    super(message);
  }

  /**
   * メッセージと原因例外付きコンストラクタ
   * @param message エラーメッセージ
   * @param cause 原因例外
   */
  public TestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * 原因例外付きコンストラクタ
   * @param cause 原因例外
   */
  public TestException(Throwable cause) {
    super(cause);
  }

  /**
   * 全パラメータ付きコンストラクタ
   * @param message エラーメッセージ
   * @param cause 原因例外
   * @param enableSuppression 抑制の有効化
   * @param writableStackTrace スタックトレースの書き込み可能性
   */
  public TestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}