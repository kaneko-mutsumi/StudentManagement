/**
 * 共通JavaScript機能（common.js）
 * 全ページで使用される共通機能
 *
 * このファイルは全てのページで共通して使用される基本的な機能を提供します
 * - メッセージの自動非表示
 * - 文字列の正規化
 * - フォーム送信時のローディング表示
 */

function initAutoHideMessages() {
  document.addEventListener('DOMContentLoaded', function() {
    const messages = document.querySelectorAll('.message');
    messages.forEach(function(message) {
      setTimeout(function() {
        message.style.opacity = '0';
        setTimeout(function() {
          message.style.display = 'none';
        }, 500);
      }, 5000);
    });
  });
}

/**
 * 文字列のスペース正規化
 * 半角スペースを全角スペースに変換
 *
 * 【用途】
 * 名前やカナ名で半角スペースと全角スペースが混在しないようにする
 */
function normalizeSpaces(text) {
  if (!text) return '';
  return text.replace(/ /g, '　'); // 半角スペースを全角スペースに変換
}

/**
 * ひらがなをカタカナに変換
 *
 * 【動作原理】
 * 文字コードを利用してひらがなをカタカナに変換
 * ひらがな「あ」(12354) → カタカナ「ア」(12450) なので +96
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

/**
 * フォーム入力の自動変換設定
 * 名前とカナ名の入力フィールドに自動変換機能を適用
 *
 * 【適用される変換】
 * - 名前フィールド：半角スペース → 全角スペース
 * - カナ名フィールド：ひらがな → カタカナ + 半角スペース → 全角スペース
 */
function initFormAutoConvert() {
  document.addEventListener('DOMContentLoaded', function() {
    const nameInput = document.querySelector('input[name="name"]');
    const kanaNameInput = document.querySelector('input[name="kanaName"]');

    // 名前フィールドのスペース変換
    if (nameInput) {
      nameInput.addEventListener('input', function() {
        this.value = normalizeSpaces(this.value);
      });
    }

    // カナ名フィールドのひらがな→カタカナ変換 + スペース変換
    if (kanaNameInput) {
      kanaNameInput.addEventListener('input', function() {
        // ひらがなをカタカナに変換
        let converted = convertToKatakana(this.value);
        // 半角スペースを全角スペースに変換
        this.value = normalizeSpaces(converted);
      });
    }
  });
}

/**
 * ローディング表示機能
 * フォーム送信時にローディングを表示してダブルクリック防止
 *
 * 【動作】
 * 1. フォーム送信ボタンがクリックされた時
 * 2. ボタンを無効化して「処理中...」に変更
 * 3. 二重送信を防止
 */
function initLoadingIndicator() {
  document.addEventListener('DOMContentLoaded', function() {
    const forms = document.querySelectorAll('form');

    forms.forEach(function(form) {
      form.addEventListener('submit', function() {
        const submitButtons = form.querySelectorAll('button[type="submit"]');
        submitButtons.forEach(function(button) {
          button.disabled = true;
          button.textContent = '処理中...';
        });
      });
    });
  });
}

/**
 * 初期化関数
 * ページ読み込み時に各機能を初期化
 *
 * 【実行される機能】
 * 1. メッセージの自動非表示
 * 2. フォームの自動変換
 * 3. ローディング表示
 */
function initCommonFeatures() {
  initAutoHideMessages();
  initFormAutoConvert();
  initLoadingIndicator();
}

// ページ読み込み時に共通機能を初期化
initCommonFeatures();