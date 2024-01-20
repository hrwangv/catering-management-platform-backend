package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @Author : haoranwang
 * @Date :
 * @Description :
 * @Version :
 */
@RestController //ResponseBody + Controller
@RequestMapping("/admin/common")  //SpringMVC设置访问路径
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    //返回类型为菜品图片的阿里云链接
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upLoad(MultipartFile file) { //形参名注意要与前端传过来的文件名保持一致。如果不一致，通过注解指定。形参类型为Spring中的一个api
        log.info("文件上传, {}",file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成新的文件名，包括随机生成的uuid和原始后缀名的拼接
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);

        } catch (IOException e) {
            log.error("文件上传失败{}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);

    }

}
