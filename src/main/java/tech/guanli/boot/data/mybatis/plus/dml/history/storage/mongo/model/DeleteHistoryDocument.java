package tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document("#{@mybatisPlusDmlHistoryMongoStorageAutoConfigurationProperty.deleteDocumentName}")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteHistoryDocument extends BasicDataHistory {

}
