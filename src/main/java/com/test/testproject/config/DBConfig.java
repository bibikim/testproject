package com.test.testproject.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@MapperScan
@PropertySource(value={"classpath:application.yml"})
@EnableTransactionManagement
@Configuration
public class DBConfig {


	@Value(value="${spring.datasource.hikari.driver-class-name}")
	private String driverClassName;
	
	@Value(value="${spring.datasource.hikari.jdbc-url}")
	private String jdbcUrl;
	
	@Value(value="${spring.datasource.hikari.username}")
	private String username;
	
	@Value(value="${spring.datasource.hikari.password}")
	private String password;
	
//	@Value(value="${mybatis.mapper-locations}")
//	private String mapperLocations;
	
	@Value(value="${mybatis.config-location}")
	private String configLocation;
	
	// hikari풀 사용하기 위한 세팅
	@Bean
	public HikariConfig config() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		return config;		// yml파일 읽어들임
	}
	
	@Bean(destroyMethod="close")
	public HikariDataSource dataSource() {
		return new HikariDataSource(config());
	}
	
	// mybatis 사용 위한 세팅
	@Bean
	public SqlSessionFactory factory() throws Exception {
		SqlSessionFactoryBean bean  = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource());  // conncetionPool 정보 넘겨주기.    hikariCP가 지원하는 datasource
//		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));   // mapper.xml 위치 // mapper가 여러개가 나올 것이므로 es!
		bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(configLocation));  // mybatis-config.xml의 위치 // config
		return bean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(factory()); 
	}
	
	@Bean
	public TransactionManager transactionManger() {
		return new DataSourceTransactionManager(dataSource());
	}
	
}
