package io.github.lionheartlattice.entity.parent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * OSS 上传结果 DTO
 */
@Data
@Accessors(chain = true)
@Schema(description = "OSS上传结果")
public class OssPutRet {
    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "MIME 类型")
    private String contentType;

    @Schema(description = "访问链接")
    private String url;

    @Schema(description = "文件路径KEY(不含域名)")
    private String key;
}
