package tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.Data;

@Data
public abstract class BasicDataHistory {

	public static final String DELETE_OPERATE = "DELETE";

	public static final String UPDATE_OPERATE = "UPDATE";

	@MongoId
	private String id;

	private LocalDateTime operateTime;

	private String operateType;

	private Object operator;

	private String table;

	private Object historyData;
}
