package tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "tech.guanli.boot.dml-history.storage.mongo")
public class MybatisPlusDmlHistoryMongoStorageAutoConfigurationProperty {

	private String deleteDocumentName = "delete-history";

	private String updateDocumentName = "update-history";
}
