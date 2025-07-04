function calculateEndDate() {
  const courseName = document.getElementById('courseName').value;
  const startDateValue = document.getElementById('courseStartAt').value;

  if (!courseName || !startDateValue) {
    return;
  }

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
      months = 6;
      break;
  }

  const startDate = new Date(startDateValue);
  const endDate = new Date(startDate);
  endDate.setMonth(endDate.getMonth() + months);

  document.getElementById('courseEndAt').value = endDate.toISOString().split('T')[0];
}

/**
 * ひらがなをカタカナに変換する関数
 */
function convertToKatakana(text) {
  if (!text) return '';

  let result = '';
  for (let i = 0; i < text.length; i++) {
    let char = text[i];
    let code = char.charCodeAt(0);

    // ひらがなの範囲（あ=12354 〜 ん=12435）
    if (code >= 12354 && code <= 12435) {
      // カタカナに変換（+96）
      result += String.fromCharCode(code + 96);
    } else {
      // ひらがな以外はそのまま
      result += char;
    }
  }
  return result;
}

// 変更検知機能
let originalValues = {};

// 名前とカナ名の入力時にリアルタイム変換
document.addEventListener('DOMContentLoaded', function() {
  const nameInput = document.querySelector('input[name="name"]');
  const kanaNameInput = document.querySelector('input[name="kanaName"]');

  // 編集画面での初期値設定（重要！）
  setInitialDateValues();

  // 変更検知機能の初期化
  initializeChangeDetection();

  // 名前フィールドのスペース変換
  if (nameInput) {
    nameInput.addEventListener('input', function() {
      this.value = this.value.replace(/ /g, '　');
      checkForChanges(); // 変更検知
    });
  }

  // カナ名フィールドのひらがな→カタカナ変換 + スペース変換
  if (kanaNameInput) {
    kanaNameInput.addEventListener('input', function() {
      // ひらがなをカタカナに変換
      let converted = convertToKatakana(this.value);
      // 半角スペースを全角スペースに変換
      this.value = converted.replace(/ /g, '　');
      checkForChanges(); // 変更検知
    });
  }
});

/**
 * 変更検知機能の初期化
 */
function initializeChangeDetection() {
  // すべての入力フィールドの初期値を保存
  const formInputs = document.querySelectorAll('input, select, textarea');

  formInputs.forEach(function(input) {
    // hiddenフィールドは除外
    if (input.type !== 'hidden') {
      originalValues[input.name] = input.value;

      // 入力イベントリスナーを追加
      input.addEventListener('input', checkForChanges);
      input.addEventListener('change', checkForChanges);
    }
  });

  console.log('初期値保存:', originalValues);
}

function checkForChanges() {
  const formInputs = document.querySelectorAll('input, select, textarea');
  const changes = [];

  formInputs.forEach(function(input) {
    if (input.type !== 'hidden' && input.name in originalValues) {
      const originalValue = originalValues[input.name];
      const currentValue = input.value;

      if (originalValue !== currentValue) {
        // 変更されたフィールドにスタイルを適用
        input.classList.add('changed');

        // 変更内容を記録
        const fieldLabel = getFieldLabel(input.name);
        changes.push({
          field: fieldLabel,
          original: originalValue || '（空）',
          current: currentValue || '（空）'
        });
      } else {
        // 変更されていないフィールドからスタイルを削除
        input.classList.remove('changed');
      }
    }
  });

  // 変更サマリーを更新
  updateChangeSummary(changes);
}

/**
 * フィールド名から日本語ラベルを取得
 */
function getFieldLabel(fieldName) {
  const labels = {
    'name': '名前',
    'kanaName': 'カナ名',
    'nickname': 'ニックネーム',
    'age': '年齢',
    'sex': '性別',
    'area': '地域',
    'email': 'メールアドレス',
    'courseName': 'コース名',
    'courseStartAt': '開始日',
    'courseEndAt': '終了日',
    'remark': '備考'
  };

  return labels[fieldName] || fieldName;
}

/**
 * 変更サマリーを更新
 */
function updateChangeSummary(changes) {
  let summaryDiv = document.querySelector('.change-summary');

  // サマリーDivが存在しない場合は作成
  if (!summaryDiv) {
    summaryDiv = document.createElement('div');
    summaryDiv.className = 'change-summary';

    // フォームの上に挿入
    const form = document.querySelector('form');
    form.parentNode.insertBefore(summaryDiv, form);
  }

  if (changes.length > 0) {
    summaryDiv.classList.add('has-changes');

    let html = '<h3>変更された項目 (' + changes.length + '件)</h3>';
    html += '<ul class="change-list">';

    changes.forEach(function(change) {
      html += '<li><strong>' + change.field + '</strong>: ';
      html += '"' + change.original + '" → "' + change.current + '"</li>';
    });

    html += '</ul>';
    summaryDiv.innerHTML = html;
  } else {
    summaryDiv.classList.remove('has-changes');
  }
}

/**
 * 編集画面での日付フィールドの初期値設定
 * Thymeleafの値が正しく設定されない場合の対処
 */
function setInitialDateValues() {
  // サーバーサイドから渡された値を取得
  const startAtElement = document.querySelector('input[name="courseStartAt"]');
  const endAtElement = document.querySelector('input[name="courseEndAt"]');

  // もし値が空の場合、data属性から取得を試みる
  if (startAtElement && !startAtElement.value) {
    const startAtValue = startAtElement.getAttribute('data-initial-value');
    if (startAtValue) {
      startAtElement.value = startAtValue;
    }
  }

  if (endAtElement && !endAtElement.value) {
    const endAtValue = endAtElement.getAttribute('data-initial-value');
    if (endAtValue) {
      endAtElement.value = endAtValue;
    }
  }
}