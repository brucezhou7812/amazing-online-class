package nz.co.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.AliyunOSSConifg;
import nz.co.service.FileService;
import nz.co.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private AliyunOSSConifg ossConfig;
    @Override
    public String uploadUserImage(MultipartFile file) {
        String bucketname = ossConfig.getBucketname();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySercret();
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        String originFileName = file.getOriginalFilename();
        String extendsion = originFileName.substring(originFileName.lastIndexOf('.'));
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY/MM/DD");
        String directory = dtf.format(ldt);
        String fileName = CommonUtils.generateUUID();
        String newFileName = "user/"+directory+"/"+ fileName+extendsion;
        try {
            PutObjectResult result = ossClient.putObject(bucketname,newFileName,file.getInputStream());
            if(result!=null){
                String url = "https://"+bucketname+"."+endpoint+"/"+newFileName;
                return url;
            }
        } catch (IOException e) {
            log.error("file upload failure.",e);
        }finally {
            ossClient.shutdown();
        }

        return null;
    }
}
