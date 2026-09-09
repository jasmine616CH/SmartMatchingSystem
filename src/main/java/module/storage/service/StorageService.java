package module.storage.service;

public interface StorageService {

    /**
     * 上传文件
     *
     */
    String uploadFile();

    /**
     * 删除文件
     *
     */
    void deleteFile();

    /**
     * 预览文件
     *
     */
    void previewFile();

    /**
     * 下载文件
     *
     */
    void downloadFile();
}
