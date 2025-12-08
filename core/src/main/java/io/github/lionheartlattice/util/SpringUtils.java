package io.github.lionheartlattice.util;

import io.github.lionheartlattice.configuration.spring.DynamicBeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Spring工具类
 *
 * @author lionheart
 */
@Component
public class SpringUtils {

    public static ApplicationContext getApplicationContext() {
        return DynamicBeanFactory.getApplicationContext();
    }

    public static ListableBeanFactory getBeanFactory() {
        return DynamicBeanFactory.getBeanFactory();
    }


    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        return DynamicBeanFactory.getConfigurableBeanFactory();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * 获取ObjectProvider（延迟加载）
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        return getBeanFactory().getBeanProvider(clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 获取所有Bean（包含父工厂）
     */
    public static <T> Map<String, T> getBeansOfTypeIncludingAncestors(Class<T> type) {
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(getBeanFactory(), type);
    }

    /**
     * 根据注解获取Bean名称
     */
    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }

    /**
     * 判断Bean是否存在
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 获取环境变量
     */
    public static Environment getEnvironment() {
        return getApplicationContext().getEnvironment();
    }

    /**
     * 获取配置属性
     */
    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    /**
     * 获取配置属性（带默认值）
     */
    public static String getProperty(String key, String defaultValue) {
        return getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取当前激活的Profile
     */
    public static String[] getActiveProfiles() {
        return getEnvironment().getActiveProfiles();
    }

    public static <T> void registerBean(String beanName, T bean) {
        DynamicBeanFactory.registerBean(beanName, bean);
    }

    public static <T> void registerBean(T bean) {
        DynamicBeanFactory.registerBean(bean);
    }
}
