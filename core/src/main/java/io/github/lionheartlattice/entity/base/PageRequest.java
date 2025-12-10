package io.github.lionheartlattice.entity.base;

import io.github.lionheartlattice.util.parent.ParentCloneable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页请求参数", example = "{\"pageIndex\":1,\"pageSize\":20,\"orders\":[{\"property\":\"id\",\"asc\":false}]}")
public class PageRequest<T> extends ParentCloneable<T> {
    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageIndex = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "排序字段列表")
    private List<InternalOrder> orders;

    @Data
    @Schema(description = "排序字段")
    public static class InternalOrder {
        @Schema(description = "排序属性名", example = "id")
        private String property;

        @Schema(description = "是否升序", example = "false")
        private boolean asc = false;
    }
}
