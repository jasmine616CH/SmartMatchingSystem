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

/**
 * 附件管理控制器
 */
@RequestMapping("/api/part/attachment")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartAttachmentController {
    
    //获取附件列表
    @GetMapping("/list")
    public Result<?> queryAttachmentList() {
        return Result.success();
    }

    //附件上传
    @PostMapping("")
    public Result<?> uploadAttachment(MultipartFile file) {
        return Result.success();
    }

    //下载附件
    @GetMapping("/download")
    public Result<?> downloadAttachment() {
        return Result.success();
    }

    //删除附件
    @DeleteMapping("/delete")
    public Result<?> deleteAttachment() {
        return Result.success();
    }
}
