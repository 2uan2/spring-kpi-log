package com.atviettelsolutions.config.jpa;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

public class JpaKpiLogDatabaseManager {
    private static EntityManagerFactory entityManagerFactory;
    private static Environment env;
    private static DataSource dataSource;
    private static ApplicationContext applicationContext;
    @PostConstruct
    public void init() {
        JpaKpiLogDatabaseManager.env = applicationContext.getEnvironment();
        JpaKpiLogDatabaseManager.dataSource = getDataSource(env);
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource);
        emfBean.setPackagesToScan("com.atviettelsolutions.domain");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        emfBean.setJpaVendorAdapter(vendorAdapter);
        emfBean.setJpaProperties(getJpaProperties(env));
        emfBean.afterPropertiesSet();
        entityManagerFactory = emfBean.getObject();
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        JpaKpiLogDatabaseManager.applicationContext = applicationContext;
    }

    private DataSource getDataSource(Environment env) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String databaseType = env.getProperty("kpi.database", "postgresql");
        switch (databaseType) {
            case "mysql":
                dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case "mariadb":
                dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                break;
            default:
                dataSource.setDriverClassName("org.postgresql.Driver");
                break;
        }
        dataSource.setUrl(env.getProperty("kpi.datasource.url"));
        dataSource.setUsername(env.getProperty("kpi.datasource.username"));
        dataSource.setPassword(env.getProperty("kpi.datasource.password", ""));
        return dataSource;
    }

    public static EntityManager getEntityManger() {
        return entityManagerFactory.createEntityManager();
    }
    private Properties getJpaProperties(Environment env) {
        Properties properties = new Properties();
        String databaseType = env.getProperty("kpi.database", "postgresql");
        switch (databaseType) {
            case "mysql":
                properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
                break;
            case "mariadb":
                properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
                break;
            default:
                properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                break;
        }
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("kpi.hibernate.ddl-auto", "update"));
        properties.setProperty("hibernate.show_sql", env.getProperty("kpi.hibernate.show_sql", "true"));
        properties.setProperty("hibernate.format_sql", env.getProperty("kpi.hibernate.format_sql", "true"));
        properties.setProperty("hibernate.packagesToScan", "com.atviettelsolutions.domain");
        return properties;
    }
    public static <T> T save(T entity) {
        EntityManager em = getEntityManger();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
