package com.westee.cake.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import com.westee.cake.exceptions.HttpException;
import com.westee.cake.generate.User;
import com.westee.cake.realm.JWTUtil;
import com.westee.cake.realm.LoginType;
import com.westee.cake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/upload/")
public class UploadFileController {
    // 七牛云配置
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String BUCKET_NAME = "";

    private  final UserService userService;

    @Autowired
    public UploadFileController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("image")
    protected void doPost(@RequestHeader("Token") String  token, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = JWTUtil.getUsername(token);
        String tokenType = JWTUtil.getTokenType(token);
        User user;
        if(Objects.equals(tokenType, LoginType.WECHAT_LOGIN.getType())) {
            user = userService.getByOpenid(username);
        } else {
            user = userService.getUserByName(username);
        }
        if(Objects.isNull(user)) {
            throw HttpException.notFound("用户不存在");
        }

        String patternFormat = "^.*\\.(jpg|png)$";
        Pattern pattern = Pattern.compile(patternFormat);
        // 初始化配置
        Configuration cfg = new Configuration(Region.regionCnEast2());
        UploadManager uploadManager = new UploadManager(cfg);

        // 生成上传凭证
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String uploadToken = auth.uploadToken(BUCKET_NAME);

        // 获取上传文件数据
        Part filePart = request.getPart("file");
        filePart.getSubmittedFileName();
        String imageName = filePart.getSubmittedFileName();
        String substring = imageName.substring(imageName.lastIndexOf("."));
        String submittedFileName = new Date() + "--" + user.getId() + substring;

        // 上传文件到七牛云
        try (InputStream fileData = filePart.getInputStream()) {
            byte[] data = IOUtils.toByteArray(fileData);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
            Response put = uploadManager.put(byteInputStream, submittedFileName, uploadToken, null, null);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(put.bodyString());
        } catch (QiniuException ex) {
            response.setStatus(ex.code());
            response.getWriter().println(ex.getMessage());
        } finally {
            response.getWriter().close();
        }
    }
}
