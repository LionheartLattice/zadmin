package io.github.lionheartlattice.util.parent;

import io.github.lionheartlattice.util.NullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ResolvableType;

public abstract class ParentController<T> extends NullUtil {
    /**
     *
     * 日志实例，使用实际子类的 Class
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 获取实体类的泛型类型
     *
     * @return 泛型类型 Class
     */
    @SuppressWarnings("unchecked")
    protected Class<T> entityClass() {
        return (Class<T>) ResolvableType.forClass(getClass()).as(ParentUtil.class)
                .getGeneric(0).resolve();
    }

}
