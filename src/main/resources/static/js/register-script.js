/**
 * コース終了日を自動計算する関数
 * コース名と開始日から適切な終了日を設定
 */
function calculateEndDate() {
  const courseName = document.getElementById('courseName').value;
  const startDateValue = document.getElementById('courseStartAt').value;

  // どちらかが未入力の場合は処理しない
  if (!courseName || !startDateValue) {
    return;
  }

  // コース別の期間設定
  let months;
  switch(courseName) {
    case 'Java入門':
      months = 3;
      break;
    case 'Spring実践':
      months = 6;
      break;
    case 'Webアプリ開発':
      months = 8;
      break;
    default:
      months = 6; // デフォルト値
      break;
  }

  // 終了日を計算
  const startDate = new Date(startDateValue);
  const endDate = new Date(startDate);
  endDate.setMonth(endDate.getMonth() + months);

  // フォームに設定
  document.getElementById('courseEndAt').value = endDate.toISOString().split('T')[0];
}

/**
 * 名前とカナ名の入力時にスペースを自動変換
 * 半角スペースを全角スペースに統一
 */
document.addEventListener('DOMContentLoaded', function() {
  const nameInput = document.querySelector('input[name="name"]');
  const kanaNameInput = document.querySelector('input[name="kanaName"]');

  /**
   * スペース正規化関数
   * @param {HTMLInputElement} input - 対象の入力フィールド
   */
  function normalizeSpaces(input) {
    input.addEventListener('input', function() {
      // 半角スペースを全角スペースに変換
      this.value = this.value.replace(/ /g, '　');
    });

    // フォーカスアウト時にも実行（確実性を高める）
    input.addEventListener('blur', function() {
      this.value = this.value.replace(/ /g, '　');
    });
  }

  // 名前フィールドにスペース正規化を適用
  if (nameInput) {
    normalizeSpaces(nameInput);
  }

  // カナ名フィールドにスペース正規化を適用
  if (kanaNameInput) {
    normalizeSpaces(kanaNameInput);
  }

  // フォーム送信前の最終チェック
  const form = document.querySelector('form');
  if (form) {
    form.addEventListener('submit', function(e) {
      // 名前とカナ名の最終スペース正規化
      if (nameInput) {
        nameInput.value = nameInput.value.replace(/ /g, '　');
      }
      if (kanaNameInput) {
        kanaNameInput.value = kanaNameInput.value.replace(/ /g, '　');
      }
    });
  }
});

/**
 * 入力値のリアルタイムバリデーション（オプション）
 */
document.addEventListener('DOMContentLoaded', function() {
  // 年齢の範囲チェック
  const ageInput = document.querySelector('input[name="age"]');
  if (ageInput) {
    ageInput.addEventListener('input', function() {
      const age = parseInt(this.value);
      if (age < 16 || age > 100) {
        this.setCustomValidity('年齢は16歳以上100歳以下で入力してください');
      } else {
        this.setCustomValidity('');
      }
    });
  }

  // メールアドレスの形式チェック（ブラウザ標準に加えて）
  const emailInput = document.querySelector('input[name="email"]');
  if (emailInput) {
    emailInput.addEventListener('blur', function() {
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (this.value && !emailPattern.test(this.value)) {
        this.setCustomValidity('正しいメールアドレスの形式で入力してください');
      } else {
        this.setCustomValidity('');
      }
    });
  }
});