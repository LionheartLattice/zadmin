package io.github.lionheartlattice.entity.base;

import io.github.lionheartlattice.util.parent.ParentCloneable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页请求参数")
public class PageDTO extends ParentCloneable<PageDTO> {
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageIndex = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "下载空excel供导入,download专用", example = "false")
    private boolean downloadEmptyExcel = false;

    @Schema(description = "搜索字段列表")
    private List<InternalSearch> searches;

    @Schema(description = "排序字段列表")
    private List<InternalOrder> orders;

    @Data
    @Schema(description = "搜索字段")
    public static class InternalSearch {
        @Schema(description = "搜索属性名", example = "username")
        private String property;

        @Schema(description = "是否like模糊查询", example = "false")
        private boolean like;

        @Schema(description = "搜索值", example = "张三")
        private String value;
    }

    @Data
    @Schema(description = "排序字段")
    public static class InternalOrder {
        @Schema(description = "排序属性名", example = "id")
        private String property = "id";

        @Schema(description = "是否升序", example = "false")
        private boolean asc = false;
    }


}
