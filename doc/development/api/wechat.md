# 微信 API 开发文档

## 微信小程序登录 login


- 访问路径: `/api/wechat/mp/login`
- 数据类型: `application/x-www-form-urlencoded`

示例

```javascript
wx.login({
  success (res) {
    if (res.code) {
      //发起网络请求
      wx.request({
        url: 'https://example.com/api/wechat/mp/login?code=' + res.code,
      })
    } else {
      console.log('登录失败！' + res.errMsg)
    }
  }
})
```

若当前的微信用户存在，则返回用户 id，同时会在 Header 返回该用户的 Jwt 信息，带着 Jwt，此时可以访问任何与用户相关的 API 接口了。

```json
  {"state":"ok","userId":100}
```
若没有 userId 字段，则表示该用户从未注册过。

## 获取微信信息注册到系统

- 访问路径: `/api/wechat/mp/register`
- 数据类型: `application/x-www-form-urlencoded`

```javascript
    
    // 推荐使用wx.getUserProfile获取用户信息，开发者每次通过该接口获取用户个人信息均需用户确认
    // 开发者妥善保管用户快速填写的头像昵称，避免重复弹窗
    wx.getUserProfile({
      desc: '用于完善会员资料', // 声明获取用户个人信息后的用途，后续会展示在弹窗中，请谨慎填写
      success: (res) => {
          wx.request({
              url: 'https://example.com/api/wechat/mp/register',
              method:'post',
              data:res
          })
      }
    })
```

注册成功

```json
  {"state":"ok","userId":100}
```

注册成功后，同时会在 header 返回 Jwt 信息，带着 Jwt，此时可以访问任何与用户相关的 API 接口了。

## 获取微信手机号码

- 访问路径: `/api/wechat/mp/phoneNumber`
- 数据类型: `application/x-www-form-urlencoded`

```html
<button open-type="getPhoneNumber" bindgetphonenumber="getPhoneNumber"></button>
```
```javascript
Page({
    getPhoneNumber (res) {
        wx.request({
            url: 'https://example.com/api/wechat/mp/phoneNumber',
            method:'post',
            data:res
        })
    }
})

```

注册成功

```json
  {"state":"ok","userId":100}
```

注册成功后，同时会在 header 返回 Jwt 信息，带着 Jwt，此时可以访问任何与用户相关的 API 接口了。