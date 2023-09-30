package com.leco.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author greg
 * @version 2023/9/29
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileUploadTests {

    @Value("${aliyun.accessKeyId}")
    private String aliAccessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String aliAccessKeySecret;

    @Value("${aliyun.oss.endpoint}")
    private String aliOssEndpoint;

    @Value("${gulimall.testImgPath}")
    private String testImgPath;

    @Value("${gulimall.testUploadName}")
    private String testUploadName;

    @Value("${gulimall.testImgPath2}")
    private String testImgPath2;

    @Value("${gulimall.testUploadName2}")
    private String testUploadName2;

    @Value("${aliyun.oss.bucketName}")
    private String aliOssBucketName;

    @Autowired
    private OSSClient ossClient;

    @Test
    public void testUpload2() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream(testImgPath2);

        ossClient.putObject(aliOssBucketName, testUploadName2, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传成功...");
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = aliOssEndpoint;
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = aliAccessKeyId;
        String accessKeySecret = aliAccessKeySecret;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream(testImgPath);

        ossClient.putObject(aliOssBucketName, testUploadName, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传成功...");
    }
}
