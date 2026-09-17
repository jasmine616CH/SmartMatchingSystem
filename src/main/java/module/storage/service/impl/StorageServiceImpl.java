package module.storage.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import module.storage.dto.PartAttachmentDTO;
import module.storage.entity.PartAttachment;
import module.storage.mapper.PartAttachmentMapper;
import module.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    public StorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Autowired
    private PartAttachmentMapper partAttachmentMapper;

    /**
     * 上传文件
     * @param file 上传文件
     * @param dto 上传文件参数
     */
    @Override
    public void uploadFile(MultipartFile file, PartAttachmentDTO dto,String objectName) throws Exception{

        //1.参数校验
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (dto == null || dto.getUserId() == null){
            throw new IllegalArgumentException("上传文件参数不完整");
        }

        //2.数据库查重
        String originalFilename = file.getOriginalFilename();
        long row = partAttachmentMapper.selectCount(
                Wrappers.<PartAttachment>lambdaQuery()
                        .eq(PartAttachment::getFileName, originalFilename)
        );
        if (row > 0){
            throw new RuntimeException("文件名已经存在，请尝试重新命名");
        }

        //3.文件上传
        minioClient.putObject(
            PutObjectArgs.builder()
                    .bucket("ug-vc")
                    .object(objectName+ "/"+ dto.getUserId()+ "/"+ originalFilename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
        );

        //4.数据库更新
        PartAttachment entity = new PartAttachment();
        entity.setPartId(dto.getPartId());
        entity.setUserId(dto.getUserId());
        entity.setFileName(originalFilename);
        entity.setFileType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setObjectName(objectName+ "/"+ dto.getUserId()+ "/"+ originalFilename);

        partAttachmentMapper.insertByFileName(entity);
    }

    /**
     * 删除文件
     * @param fileName 文件名
     * @param userId 用户id
     */
    @Override
    public void deleteFile(String fileName , Long userId) {

        //1.查找文件
        Long row = partAttachmentMapper.selectCount(
                Wrappers.<PartAttachment>lambdaQuery()
                        .eq(PartAttachment::getFileName, fileName)
                        .eq(PartAttachment::getUserId, userId)
        );
        if (row != 1) {
            throw new RuntimeException("请输入准确信息");
        }

        //2.软删除
        partAttachmentMapper.update(
                Wrappers.<PartAttachment>lambdaUpdate()
                        .eq(PartAttachment::getFileName, fileName)
                        .eq(PartAttachment::getUserId, userId)
                        .set(PartAttachment::getIsDeleted, 1)
        );

    }

    /**
     * 下载文件
     * @param fileName 文件名
     * @param userId 用户id
     * @param localFilePath 下载路径
     */
    @Override
    public void downloadFile(String fileName, Long userId, String localFilePath) {

        //1.参数校验
        if (fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("下载文件不能为空");
        }
        if (userId == null){
            throw new IllegalArgumentException("下载参数不完整");
        }

        //2.数据库查询文件信息
        PartAttachment dto = partAttachmentMapper.selectById(
                Wrappers.<PartAttachment>lambdaQuery()
                        .eq(PartAttachment::getUserId, userId)
                        .eq(PartAttachment::getFileName, fileName)
        );

        //3.下载文件
        try {
            minioClient.downloadObject(
                DownloadObjectArgs.builder()
                            .bucket("ug-vc")
                            .object(dto.getObjectName())
                            .filename(localFilePath)
                            .overwrite(true)
                            .build()
            );
        } catch (Exception e){
            throw new RuntimeException("文件下载失败");
        }

    }

    /**
     * 浏览文件
     * @param fileName 文件名字
     * @param userId 用户id
     */
    @Override
    public InputStream previewFile(String fileName, Long userId) throws Exception{

        //1.参数校验
        if (fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("下载文件不能为空");
        }
        if (userId == null){
            throw new IllegalArgumentException("下载参数不完整");
        }

        //2.数据库查询文件信息
        PartAttachment dto = partAttachmentMapper.selectById(
                Wrappers.<PartAttachment>lambdaQuery()
                        .eq(PartAttachment::getUserId, userId)
                        .eq(PartAttachment::getFileName, fileName)
        );

        //3.浏览文件
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("ug-vc")
                        .object(dto.getObjectName())
                        .build());
    }

    @Override
    public String contentType(String fileName, Long userId){

        //1.参数校验
        if (fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("下载文件不能为空");
        }
        if (userId == null){
            throw new IllegalArgumentException("下载参数不完整");
        }

        //2.数据库查询文件信息
        PartAttachment dto = partAttachmentMapper.selectById(
                Wrappers.<PartAttachment>lambdaQuery()
                        .eq(PartAttachment::getUserId, userId)
                        .eq(PartAttachment::getFileName, fileName)
        );

        return dto.getFileType();
    }
}
