package io.github.lionheartlattice.configuration.spring;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 动态Bean工厂，支持运行时注册Bean
 *
 * @author lionheart
 */
@Component
public class DynamicBeanFactory implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicBeanFactory.class);

    private static ConfigurableListableBeanFactory beanFactory;

    @Getter
    private static ApplicationContext applicationContext;

    /**
     *
     * 注册Bean（使用类名作为Bean名称）
     */
    public static void registerBean(Object bean) {
        String beanName = lowerFirst(bean.getClass().getSimpleName());
        registerBean(beanName, bean);
    }

    /**
     * 注册Bean
     */
    public static void registerBean(String beanName, Object bean) {
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory.containsBean(beanName)) {
            logger.warn("Bean [{}] 已存在，跳过注册", beanName);
            return;
        }
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
        logger.debug("注册Bean: {} -> {}", beanName, bean.getClass().getName());
    }

    /**
     * 判断Bean是否存在
     */
    public static boolean containsBean(String beanName) {
        return getConfigurableBeanFactory().containsBean(beanName);
    }

    /**
     * 获取可配置的Bean工厂
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        if (beanFactory != null) {
            return beanFactory;
        }
        if (applicationContext instanceof ConfigurableApplicationContext cac) {
            return cac.getBeanFactory();
        }
        throw new IllegalStateException("BeanFactory 未初始化");
    }

    /**
     * 获取Bean工厂（ListableBeanFactory接口）
     */
    public static ListableBeanFactory getBeanFactory() {
        return getConfigurableBeanFactory();
    }

    /**
     * 首字母小写
     */
    private static String lowerFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DynamicBeanFactory.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DynamicBeanFactory.applicationContext = applicationContext;
    }
}
