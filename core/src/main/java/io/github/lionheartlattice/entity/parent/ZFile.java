package io.github.lionheartlattice.entity.parent;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EasyAssertMessage;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.github.lionheartlattice.entity.parent.proxy.ZFileProxy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 文件存储实体
 * 主键直接存储 OSS Key (格式: yyyyMM/雪花ID(去除yyyyMM).后缀)
 */
@Data
@EntityProxy
@Table("z_file")
@Schema(description = "文件存储信息")
@EasyAssertMessage("未找到对应的文件信息")
public class ZFile extends ParentClientEntity<ZFile, ZFileProxy> implements ProxyEntityAvailable<ZFile, ZFileProxy> {

    @Column(primaryKey = true)
    @Schema(description = "文件路径KEY(格式: yyyyMM/雪花ID(去除yyyyMM).后缀)")
    private String id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "MIME类型")
    private String contentType;

    @Schema(description = "用途")
    private String usage;

    @Schema(description = "创建人ID")
    private BigDecimal createId;
}
