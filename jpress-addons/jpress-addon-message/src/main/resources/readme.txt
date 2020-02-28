用于jpress的拓展插件主要实现了用户提交留言的功能

代码示例：获取用户留言的列表
```
#messageList()
#for(msg : messageList)
<div class="msg-list">
  <div class="msguser-img"></div>
  <div class="msguser-info">
    <div class="minfo-tp">
      <div class="minfo-name">#(msg.name ??)</div>
      <!--<div class="minfo-time">2020年2月26日</div>-->
    </div>
    <div class="minfo-msg">
      <p>#(msg.message ??)</p>
    </div>
  </div>
  <div class="clear"></div>
</div>
#end
#end
```
提交留言接口地址: /msgController


![输入图片说明](https://images.gitee.com/uploads/images/2020/0226/214527_cc2a8636_2147597.png)
![输入图片说明](https://images.gitee.com/uploads/images/2020/0226/214555_963e756a_2147597.png)