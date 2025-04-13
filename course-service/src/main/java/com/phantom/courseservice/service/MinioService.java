package com.phantom.courseservice.service;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ResourceLoader resourceLoader;

    public String uploadFile(MultipartFile file,String bucketName) throws Exception{
        //上传文件
        String fileName=file.getOriginalFilename();
        // 一次性读取输入流到字节数组
        byte[] fileBytes;
        try (InputStream inputStream = file.getInputStream()) {
            fileBytes = inputStream.readAllBytes();
        } catch (Exception e) {
            throw new Exception("读取文件失败: " + e.getMessage());
        }

        //上传文件到minIO
        try(InputStream inputStream=new ByteArrayInputStream(fileBytes)){
            minioClient.putObject(
               PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream,file.getSize(),-1)
                        .contentType(file.getContentType())
                        .build()
            );
        }catch (Exception e){
            throw new Exception("上传到MinIO失败："+e.getMessage());
        }
        //保存本地副本（仅备用）
        String savePath = resourceLoader.getResource("classpath:content/").getFile().getAbsolutePath() + "/";
        File localFile=new File(savePath+fileName);
//        File directory=new File(savePath);
//        if(!directory.exists()){
//            directory.mkdir();
//        }
//        File localFile=new File(savePath+fileName);
        if (!localFile.getParentFile().exists()){
            localFile.getParentFile().mkdir();
        }
        if(!localFile.exists()){
            try(
                FileOutputStream outputStream=new FileOutputStream(localFile)){
                outputStream.write(fileBytes);
            }catch (Exception e){
                throw new Exception("保存本地副本失败： "+e.getMessage());
            }
        }
        //返回文件访问url
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
    //下载文件（返回流）
    public InputStream downloadFile(String bucketName,String fileName) throws Exception{

        try{
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            String savePath="src/main/resources/content/";
            File file=new File(savePath+fileName);
            if(file.exists()){
                return new FileInputStream(file);
            }
            throw new Exception("文件不存在于MinIo和本地： "+e.getMessage());
        }
    }
}
