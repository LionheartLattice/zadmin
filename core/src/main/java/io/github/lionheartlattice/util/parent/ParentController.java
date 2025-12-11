package io.github.lionheartlattice.util.parent;

import io.github.lionheartlattice.util.NullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ParentController extends NullUtil {
    /**
     *
     * 日志实例，使用实际子类的 Class
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());


}
