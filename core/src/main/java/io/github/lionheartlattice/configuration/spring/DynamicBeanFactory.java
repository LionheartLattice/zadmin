package io.github.lionheartlattice.configuration.spring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DynamicBeanFactory implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(DynamicBeanFactory.class);
    private static ConfigurableListableBeanFactory beanFactory;
    private static ApplicationContext applicationContext;

    public static void registerBean(Object bean) {
        String beanName = bean.getClass().getSimpleName();
        registerBean(beanName, bean);
    }

    private static boolean isBeanExists(String beanName) {
        return getConfigurableBeanFactory().containsBean(beanName);
    }

    public static void registerBean(String beanName, Object bean) {
        if (isBeanExists(beanName)) {
            logger.warn("BeanName:[ {} ] 已存在,不再注册,已忽略", beanName);
            return;
        }
        ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else {
            if (!(applicationContext instanceof ConfigurableApplicationContext)) {
                throw new RuntimeException("applicationContext is not ConfigurableApplicationContext");
            }

            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        }

        return factory;
    }

    public static ConfigurableListableBeanFactory getBeanFactory() {
        return DynamicBeanFactory.getConfigurableBeanFactory();
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
