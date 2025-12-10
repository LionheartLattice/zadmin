package io.github.lionheartlattice.entity.base;

import io.github.lionheartlattice.util.parent.ParentCloneable;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

@Data
public class PageRequest<T> extends ParentCloneable<T> {
    private Integer pageIndex = 1;
    private Integer pageSize = 20;

    @Data
    public static class InternalOrder {
        private String property;
        private boolean asc;
    }
}
