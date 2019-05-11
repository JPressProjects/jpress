package io.jpress.web.handler;


import com.jfinal.render.RenderManager;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


public class AttachmentHandlerKit {


    public static void handle(String target
            , HttpServletRequest request
            , HttpServletResponse response
            , boolean[] isHandled) {


        String attachmentRoot = JPressConfig.me.getAttachmentRoot();

        //不处理默认情况，由web容器去处理
        if (StrUtil.isBlank(attachmentRoot)){
            return;
        }

        try {

            isHandled[0] = true;

            if (target.endsWith("/") || target.indexOf(".") == -1) {
                response.sendError(404);
                return;
            }

            File file = new File(attachmentRoot,target);
            if (!file.exists()){
                response.sendError(404);
                return;
            }

            RenderManager.me().getRenderFactory()
                    .getFileRender(file)
                    .setContext(request, response)
                    .render();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}

