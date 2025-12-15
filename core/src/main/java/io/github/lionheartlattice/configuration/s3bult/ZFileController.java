package io.github.lionheartlattice.configuration.s3bult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
     * @param id 文件ID
     * @return 文件访问链接
     */
    @GetMapping("/url/{id}")
    @Operation(summary = "获取文件URL", description = "根据ID查询数据库并返回文件访问链接")
    public String getUrl(@Parameter(description = "文件ID", required = true) @PathVariable BigDecimal id) {
        return zFileService.getUrlById(id);
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件", description = "根据ID删除数据库记录及OSS中的物理文件")
    public void delete(@Parameter(description = "文件ID", required = true) @PathVariable BigDecimal id) {
        zFileService.delete(id);
    }
}
