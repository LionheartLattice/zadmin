package io.github.lionheartlattice.entity.parent;

/**
 * Jackson 视图接口，用于控制 DTO 字段的序列化和反序列化
 *
 * @author lionheart
 */
public class Views {

    /**
     * 创建视图：用于新增操作，不包含 ID 字段
     */
    public interface Create {
    }

    /**
     * 更新视图：用于更新操作，包含 ID 字段
     */
    public interface Update extends Create {
    }
}

