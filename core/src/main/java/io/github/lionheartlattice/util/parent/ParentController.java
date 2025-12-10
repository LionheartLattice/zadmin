package io.github.lionheartlattice.util.parent;

import io.github.lionheartlattice.util.NullUtil;
import io.github.lionheartlattice.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

public abstract class ParentController<S> extends NullUtil {
    /**
     *
     * 日志实例，使用实际子类的 Class
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final S service = SpringUtils.getBean(serviceClass());

    /**
     * 获取当前控制器泛型声明的 Service 类型
     */
    @SuppressWarnings("unchecked")
    protected Class<S> serviceClass() {
        return (Class<S>) ResolvableType.forClass(getClass())
                .as(ParentController.class)
                .getGeneric(0)
                .resolve();
    }

}
