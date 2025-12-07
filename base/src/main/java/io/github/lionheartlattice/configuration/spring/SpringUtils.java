package io.github.lionheartlattice.configuration.spring;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自定义Spring工具类，基于DynamicBeanFactory实现
 *
 * @author lionheart
 */
@Component
public class SpringUtils {

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return DynamicBeanFactory.getApplicationContext();
    }

    /**
     * 获取Bean工厂
     *
     * @return ListableBeanFactory
     */
    public static ListableBeanFactory getBeanFactory() {
        return DynamicBeanFactory.getBeanFactory();
    }

    /**
     * 获取可配置的Bean工厂
     *
     * @return ConfigurableListableBeanFactory
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        return DynamicBeanFactory.getConfigurableBeanFactory();
    }

    /**
     * 根据名称获取Bean
     *
     * @param name Bean名称
     * @param <T>  Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) DynamicBeanFactory.getBeanFactory().getBean(name);
    }

    /**
     * 根据类型获取Bean
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return DynamicBeanFactory.getBeanFactory().getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return DynamicBeanFactory.getBeanFactory().getBean(name, clazz);
    }

    /**
     * 获取指定类型的所有Bean
     *
     * @param type 类型
     * @param <T>  Bean类型
     * @return Bean映射
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return DynamicBeanFactory.getBeanFactory().getBeansOfType(type);
    }

    /**
     * 注册Bean
     *
     * @param beanName Bean名称
     * @param bean     Bean实例
     * @param <T>      Bean类型
     */
    public static <T> void registerBean(String beanName, T bean) {
        DynamicBeanFactory.registerBean(beanName, bean);
    }

    /**
     * 注册Bean（使用类名作为Bean名称）
     *
     * @param bean Bean实例
     * @param <T>  Bean类型
     */
    public static <T> void registerBean(T bean) {
        DynamicBeanFactory.registerBean(bean);
    }
}
