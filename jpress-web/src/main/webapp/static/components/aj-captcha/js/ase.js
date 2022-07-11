// 加密数据函数 工具crypto.js 文件工具
/**
 * @word 要加密的内容
 * @keyWord String  服务器随机返回的关键字
 *  */
function aesEncrypt(word,keyWord){
  // var keyWord = keyWord || "XwKsGlMcdPMEhR1B"
  var key = CryptoJS.enc.Utf8.parse(keyWord);
  var srcs = CryptoJS.enc.Utf8.parse(word);
  var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
  return encrypted.toString();
}
