package tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.Package;

@AutoConfiguration
@ComponentScan(basePackageClasses = { Package.class })
@EnableConfigurationProperties(MybatisPlusDmlHistoryMongoStorageAutoConfigurationProperty.class)
public class MybatisPlusDmlHistoryMongoStorageAutoConfiguration {

}
