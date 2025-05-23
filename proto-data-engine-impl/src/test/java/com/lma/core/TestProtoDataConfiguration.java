package com.lma.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.transformation.BaseTransformation;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableJpaRepositories(basePackages = "com.lma.core.proto.repository")
@TestConfiguration
public class TestProtoDataConfiguration {

    @Bean
    public BaseTransformation baseTransformation() {
        return new BaseTransformation(new ObjectMapper());
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.lma.core.proto.entity");
//        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        Properties jpaProperties = new Properties();
//
//        //Configures the used database dialect. This allows Hibernate to create SQL
//        //that is optimized for the used database.
//        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
//
//        //If the value of this property is true, Hibernate writes all SQL
//        //statements to the console.
//        jpaProperties.put("hibernate.show_sql",
//                env.getRequiredProperty("hibernate.show_sql")
//        );
//
//        em.setJpaProperties(jpaProperties);
//
//        return em;
//    }

//    @Bean
//    public DataSource dataSource(){
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/configurator_test");
//        dataSource.setUsername( "lma" );
//        dataSource.setPassword( "12345" );
//        return dataSource;
//    }

//    private Properties additionalProperties() {
//        Properties properties = new Properties();
//        properties.setProperty("hibernate.hbm2ddl.auto", "'update'");
//        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//
//        return properties;
//    }
}
