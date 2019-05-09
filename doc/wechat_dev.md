# JPress 微信插件开发

## 目录

- 微信公众号配置
- 开发JPress微信运营插件
- JPress微信运营插件的 Hello World
- 测试JPress微信运营插件

## 微信公众号配置

在开发 JPress 微信运营插件之前，建议（但不是必须）先做好微信公众号的配置，微信公众号配置请参考《JPress产品手册之微信公众号配置》章节。

同时，JPress 微信运营插件开发完毕后，必须做好微信公众号配置后，才能进行插件测试。

## 开发JPress微信运营插件

在 jpress 体系中，值需要编写任意名称的 Java 类，实现 `WechatAddon` 接口即可 ，参考如下实例：

```java
public class HelloWechatAddon implements WechatAddon {

    @Override
    public boolean onMatchingMessage(InMsg inMsg, 
        MsgController msgController) {

        return false;
    }

    @Override
    public boolean onRenderMessage(InMsg inMsg, 
        MsgController msgController) {

        return true;
    }
}
```
**说明：**

* 1、在任意maven module下，编写任意名称的类，实现WechatAddon接口。JPress 会自动扫描到该类，并添加到 JPress 的管理体系里去。
* 2、复写方法`onMatchingMessage`和`onRenderMessage`。
    * onMatchingMessage ：用来匹配是否是本插件要处理的消息
    * onRenderMessage ：用来返回给微信客户端一个消息

* 3、添加 `@WechatAddonConfig` 注解的配置，用来给这个插件添加描述。


## JPress微信运营插件的 Hello World
以下代码是完整的 hello world 例子，当用户在微信客户端（手机端），给公众号输入 `hello` 的时候，服务器给微信返回 `world` 字符串：

```java
@WechatAddonConfig(
        id = "ip.press.helloaddon", //这个插件的ID
        title = "Hello World",//这个插件的标题，用于在后台显示
        description = "这是一个 Hello World 微信插件，方便开发参考。用户输入 hello，返回 world", //这个插件的描述
        author = "海哥" //这个插件的作者
)
public class HelloWechatAddon implements WechatAddon {

    @Override
    public boolean onMatchingMessage(InMsg inMsg, MsgController msgController) {
        
        //当用户给公众号发送的不是文本消息的时候
        //返回 false 不由本插件处理
        if (!(inMsg instanceof InTextMsg)) {
            return false;
        }

        InTextMsg inTextMsg = (InTextMsg) inMsg;
        String content = inTextMsg.getContent();
        
        //当用户输入的内容不是 hello 的时候
        //返回false，不由本插件处理
        return content != null && content.equalsIgnoreCase("hello");
    }


    @Override
    public boolean onRenderMessage(InMsg inMsg, MsgController msgController) {
    
        //创建一个新的文本消息
        //通过 msgController 进行渲染返回给用户
        OutTextMsg outTextMsg = new OutTextMsg(inMsg);
        outTextMsg.setContent("world");
        msgController.render(outTextMsg);
        
        //返回 true，表示本插件已经成功处理该消息
        //若返回false，表示本插件处理消息失败，将会交给系统或者其他插件去处理
        return true;
    }
}
```
完整代码可以看这里：https://gitee.com/fuhai/jpress/blob/master/jpress-web/src/main/java/io/jpress/web/wechat/HelloWechatAddon.java

## 测试JPress微信运营插件