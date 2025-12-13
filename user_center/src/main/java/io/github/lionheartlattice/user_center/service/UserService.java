package io.github.lionheartlattice.user_center.service;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.easy.query.api.proxy.base.ClassProxy;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import com.easy.query.core.expression.builder.core.NotNullOrEmptyValueFilter;
import io.github.lionheartlattice.entity.parent.PageDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserCreatDTO;
import io.github.lionheartlattice.entity.user_center.dto.UserUpdateDTO;
import io.github.lionheartlattice.entity.user_center.po.User;
import io.github.lionheartlattice.entity.user_center.po.proxy.UserProxy;
import io.github.lionheartlattice.util.CopyUtil;
import io.github.lionheartlattice.util.response.ErrorEnum;
import io.github.lionheartlattice.util.response.ExceptionWithEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static io.github.lionheartlattice.util.NullUtil.isNotNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TransactionTemplate transactionTemplate;

    @Value("${app.auth.aes-key:LionHeartLattice}")
    private String aesKey;

    private AES aes;

    @PostConstruct
    public void init() {
        // 初始化AES工具
        this.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean create(UserCreatDTO dto) {
        String encryptPwd = aes.encryptHex(dto.getPwd());

        long rows = new User().copyFrom(dto)
                              .setUpdateId(BigDecimal.ZERO)
                              .setPwd(encryptPwd)
                              .insertable()
                              .executeRows();
        return isNotNull(rows); //如果操作异常会直接抛异常，或者数据库影响行数为0，因此只需要判定非空非0即可，下列的也是类似
    }

    public boolean encoderPwd(Long id) {
        User user = new User().queryable()
                              .whereById(id)
                              .singleNotNull();
        // 加密密码并更新到数据库(这里只是为了演示，实际开发中应该在登录时加密密码)
        String encryptPwd = aes.encryptHex(user.getPwd());

        new User().setId(user.getId())
                  .setPwd(encryptPwd)
                  .updatable()
                  .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                  .executeRows();
        return true;
    }

    public EasyPageResult<User> page(PageDTO dto) {
        return new User().queryable()
                         .filterConfigure(NotNullOrEmptyValueFilter.DEFAULT_PROPAGATION_SUPPORTS)
                         .include(UserProxy::deptList)
                         .include(UserProxy::roleList)
                         .where(isNotNull(dto.getSearches()), u -> {
                             for (PageDTO.InternalSearch search : dto.getSearches()) {
                                 switch (search.getQueryType()) {
                                     case 1 -> u.anyColumn(search.getProperty())
                                                .eq(search.getValue());
                                     case 2 -> u.anyColumn(search.getProperty())
                                                .like(search.getValue());
                                     case 3 -> {
                                         // 解析日期范围
                                         LocalDateTime[] dateRange = PageDTO.parseDateRange(search.getValue());
                                         if (isNotNull(dateRange[0])) {
                                             u.anyColumn(search.getProperty())
                                              .ge(dateRange[0]);
                                         }
                                         if (isNotNull(dateRange[1])) {
                                             u.anyColumn(search.getProperty())
                                              .le(dateRange[1]);
                                         }
                                     }
                                     default -> throw new ExceptionWithEnum(ErrorEnum.VALID_ERROR);
                                 }
                             }
                         })
                         .orderBy(isNotNull(dto.getOrders()), u -> {
                             for (PageDTO.InternalOrder order : dto.getOrders()) {
                                 u.anyColumn(order.getProperty())
                                  .orderBy(order.isAsc());
                             }
                         })
                         .toPageResult(dto.getPageIndex(), dto.getPageSize());
    }

    public Boolean update(UserUpdateDTO dto) {
        long rows = new User().copyFrom(dto)
                              .updatable()
                              .executeRows();
        return isNotNull(rows);
    }

    public Boolean delete(List<BigDecimal> ids) {
        long rows = new User().expressionDeletable()
                              .where(u -> u.id()
                                           .in(ids))
                              .executeRows();
        return isNotNull(rows);
    }


    public Boolean saveBatch(List<UserCreatDTO> dtos) {
        List<User> entities = CopyUtil.copyList(dtos, User.class);
        // 批量保存时也需要处理密码加密，这里简单处理，循环加密
        entities.forEach(user -> {
            if (user.getPwd() != null) {
                user.setPwd(aes.encryptHex(user.getPwd()));
            }
        });

        Long row = transactionTemplate.execute(status -> new User().insertable(entities)
                                                                   .batch(true)
                                                                   .executeRows());
        return isNotNull(row);
    }

    public UserUpdateDTO getById(BigDecimal id) {
        return new User().queryable()
                         .whereById(id)
                         .select(u -> new ClassProxy<>(UserUpdateDTO.class).selectAll(u))
                         .singleNotNull();
    }

}
