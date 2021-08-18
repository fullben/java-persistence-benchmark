package de.uniba.dsg.jpb.server;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "de.uniba.dsg.jpb.server.data.access.jpa")
@EnableTransactionManagement
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaConfiguration {

  private final Environment environment;

  @Autowired
  public JpaConfiguration(Environment environment) {
    this.environment = environment;
  }

  @Bean
  @Primary
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(environment.getProperty("jpb.jpa.datasource.driverClassName"));
    dataSource.setUrl(environment.getProperty("jpb.jpa.datasource.url"));
    dataSource.setUsername(environment.getProperty("jpb.jpa.datasource.username"));
    dataSource.setPassword(environment.getProperty("jpb.jpa.datasource.password"));
    return dataSource;
  }

  @Bean
  @Primary
  public EntityManagerFactory entityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("de.uniba.dsg.jpb.server.data.model.jpa");
    factory.setDataSource(dataSource());
    final String dialectKey = "jpb.jpa.database-platform";
    final String ddlAutoKey = "jpb.jpa.hibernate.ddl-auto";
    final String timeZoneKey = "jpb.jpa.properties.hibernate.jdbc.time_zone";
    Map<String, Object> props = new HashMap<>();
    props.put(dialectKey, environment.getProperty(dialectKey));
    props.put(ddlAutoKey, environment.getProperty(ddlAutoKey));
    props.put(timeZoneKey, environment.getProperty(timeZoneKey));
    factory.setJpaPropertyMap(props);
    factory.afterPropertiesSet();

    return factory.getObject();
  }

  @Bean
  @Primary
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory());
    return transactionManager;
  }
}
