/**
 * コース終了日を自動計算する関数
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
 */
document.addEventListener('DOMContentLoaded', function() {
  const nameInput = document.querySelector('input[name="name"]');
  const kanaNameInput = document.querySelector('input[name="kanaName"]');

  // 名前フィールドにスペース正規化を適用
  if (nameInput) {
    nameInput.addEventListener('input', function() {
      // 半角スペースを全角スペースに変換
      this.value = this.value.replace(/ /g, '　');
    });
  }

  // カナ名フィールドにスペース正規化を適用
  if (kanaNameInput) {
    kanaNameInput.addEventListener('input', function() {
      // 半角スペースを全角スペースに変換
      this.value = this.value.replace(/ /g, '　');
    });
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

  console.log('新規登録画面が読み込まれました');
});