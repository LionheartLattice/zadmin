package io.github.lionheartlattice.configuration.s3bult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件管理控制器
 * 提供文件的上传和删除接口
 */
@RestController
@RequestMapping("/z_file")
@RequiredArgsConstructor
@Tag(name = "文件管理")
public class ZFileController {

    private final ZFileService zFileService;

    /**
     * 上传文件并返回URL
     *
     * @param file  文件对象
     * @param usage 用途
     * @return 文件访问链接
     */
    @PostMapping(value = "/upload-url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件返回URL", description = "上传文件并直接返回可访问的HTTP链接")
    public String uploadReturnUrl(
            @Parameter(description = "文件", required = true) @RequestPart("file") MultipartFile file,
            @Parameter(description = "用途", required = false) @RequestParam(required = false) String usage) {
        return zFileService.uploadReturnUrl(file, usage);
    }

    /**
     * 获取文件URL
     *
     * @param fileKey 文件KEY
     * @return 文件访问链接
     */
    @GetMapping("/url/{fileKey}")
    @Operation(summary = "获取文件URL", description = "根据文件KEY返回文件访问链接")
    public String getUrl(@Parameter(description = "文件KEY", required = true) @PathVariable String fileKey) {
        return zFileService.getUrlByKey(fileKey);
    }

    /**
     * 删除文件
     *
     * @param fileKey 文件KEY
     */
    @DeleteMapping("/{fileKey}")
    @Operation(summary = "删除文件", description = "根据文件KEY删除数据库记录及OSS中的物理文件")
    public void delete(@Parameter(description = "文件KEY", required = true) @PathVariable String fileKey) {
        zFileService.delete(fileKey);
    }
}
