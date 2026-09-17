package module.part.controller;

import jakarta.servlet.http.HttpServletResponse;
import module.storage.dto.PartAttachmentDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.storage.service.StorageService;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * 附件管理控制器
 */
@RequestMapping("/api/part/attachment")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartAttachmentController {


    private final StorageService storageService;


    /**
     * 附件上传接口
     * 
     */
    @PostMapping("/upload")
    public Result<?> uploadAttachment(MultipartFile file, PartAttachmentDTO dto, String objectName) {
        try {
            storageService.uploadFile(file, dto, objectName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.success();
    }

    /**
     * 删除附件接口
     * 
     */
    @DeleteMapping("/delete")
    public Result<?> deleteAttachment(String fileName , Long userId) {
        storageService.deleteFile(fileName, userId);
        return Result.success();
    }

    /**
     * 附件预览接口
     * 
     */
    @GetMapping("/preview/{fileName}")
    public Result<Void> previewAttachment(@PathVariable String fileName,Long userId, HttpServletResponse response) throws Exception{
        try (InputStream stream = storageService.previewFile(fileName, userId)) {
            response.setContentType(storageService.contentType(fileName, userId));
            byte[] buffer = new byte[8192];
            int bytesRead;
            OutputStream out = response.getOutputStream();
            while ((bytesRead = stream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
        return null;
    }

    /**
     * 附件下载接口
     * 
     */
    @GetMapping("/download/{attachmentId}")
    public Result<?> downloadAttachment(String fileName, Long userId, @PathVariable String attachmentId) {
        storageService.downloadFile(fileName, userId, attachmentId);
        return Result.success("文件下载成功");
    }

}
