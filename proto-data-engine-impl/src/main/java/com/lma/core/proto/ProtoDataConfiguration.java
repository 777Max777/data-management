package com.lma.core.proto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lma.core.proto.transformation.BaseTransformation;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableJpaRepositories(basePackages = "com.lma.core.proto.repository")
@Configuration
@EnableTransactionManagement
public class ProtoDataConfiguration {

//    @Bean
//    @ConfigurationProperties(prefix = "spring.internal-datasource")
//    public DataSource internalDatasource() {
//        return DataSourceBuilder.create().build();
//    }

    @Bean
    public BaseTransformation baseTransformation() {
        return new BaseTransformation(new ObjectMapper());
    }

    @Bean
    public DataSource dataSource(Environment env) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        dataSource.setUsername(env.getRequiredProperty("spring.datasource.username"));
        dataSource.setPassword(env.getRequiredProperty("spring.datasource.password"));
        dataSource.setUrl(env.getRequiredProperty("spring.datasource.url"));

        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.lma.core.proto.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();

        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        jpaProperties.put("hibernate.show_sql",
                env.getRequiredProperty("hibernate.show_sql")
        );

        jpaProperties.put("spring.jpa.hibernate.ddl-auto",
                env.getRequiredProperty("spring.jpa.hibernate.ddl-auto")
        );

        em.setJpaProperties(jpaProperties);

        return em;
    }
}
