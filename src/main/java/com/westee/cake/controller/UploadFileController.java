package com.westee.cake.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/upload/")
public class UploadFileController {
    // 七牛云配置
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String BUCKET_NAME = "";

    @PostMapping("image")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // 初始化配置
        Configuration cfg = new Configuration(Region.regionCnEast2());
        UploadManager uploadManager = new UploadManager(cfg);

        // 生成上传凭证
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String uploadToken = auth.uploadToken(BUCKET_NAME);

        // 获取上传文件数据
        Part filePart = request.getPart("file");
        String submittedFileName = filePart.getSubmittedFileName();

        // 上传文件到七牛云
        try (InputStream fileData = filePart.getInputStream()) {
            byte[] data = IOUtils.toByteArray(fileData);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
            Response put = uploadManager.put(byteInputStream, submittedFileName, uploadToken, null, null);
            System.out.println(put.bodyString());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (QiniuException ex) {
            response.setStatus(ex.code());
            response.getWriter().println(ex.getMessage());
        } finally {
            response.getWriter().close();
        }
    }
}
