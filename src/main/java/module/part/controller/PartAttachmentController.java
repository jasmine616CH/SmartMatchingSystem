package module.part.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.storage.service.StorageService;

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
    @PostMapping("")
    public Result<?> uploadAttachment(MultipartFile file) {
        storageService.uploadFile();
        return Result.success();
    }

    /**
     * 删除附件接口
     * 
     */
    @DeleteMapping("")
    public Result<?> deleteAttachment() {
        storageService.deleteFile();
        return Result.success();
    }

    /**
     * 附件预览接口
     * 
     */
    @GetMapping("")
    public Result<?> previewAttachment() {
        return null;
    }

    /**
     * 附件下载接口
     * 
     */
    @GetMapping("/download/{attachmentId}")
    public Result<?> downloadAttachment() {
        return null;
    }

}
