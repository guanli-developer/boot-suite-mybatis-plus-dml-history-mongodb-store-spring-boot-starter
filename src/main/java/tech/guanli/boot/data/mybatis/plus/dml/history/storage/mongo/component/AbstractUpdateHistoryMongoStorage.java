package tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tech.guanli.boot.data.mybatis.plus.dml.history.component.UpdateAuditor;
import tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.model.DeleteHistoryDocument;
import tech.guanli.boot.data.mybatis.plus.dml.history.storage.mongo.model.UpdateHistoryDocument;

@Slf4j
public abstract class AbstractUpdateHistoryMongoStorage implements UpdateAuditor {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DataSource dataSource;

	@Getter(value = AccessLevel.PRIVATE)
	@Setter(value = AccessLevel.PROTECTED)
	private Object operator;

	protected abstract void setOperator();

	private String fixSql(String sql) {
		StringBuilder querySqlBuilder = new StringBuilder();
		querySqlBuilder.append("SELECT * FROM ");
		sql = sql.replace("UPDATE ", "");
		querySqlBuilder.append(sql.substring(0, sql.indexOf(" SET ")));
		if (sql.contains(" WHERE ")) {
			querySqlBuilder.append(sql.substring(sql.indexOf(" WHERE ")));
		}
		return querySqlBuilder.toString();
	}

	@Override
	public void audit(String sql) {
		try (Connection connection = dataSource.getConnection();
				PreparedStatement prepareStatement = connection.prepareStatement(fixSql(sql));
				ResultSet resultSet = prepareStatement.executeQuery();) {
			setOperator();
			ResultSetMetaData metaData = resultSet.getMetaData();
			JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
			int columnCount = metaData.getColumnCount();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (resultSet.next()) {
				ObjectNode objectNode = jsonNodeFactory.objectNode();
				for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
					String columnName;
					columnName = metaData.getColumnName(columnIndex);
					switch (metaData.getColumnClassName(columnIndex)) {
					case "java.lang.String":
						Optional.ofNullable(resultSet.getString(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.textNode(data));
						});
						break;
					case "java.lang.Integer":
						Optional.ofNullable(resultSet.getInt(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.numberNode(data));
						});
						break;
					case "java.lang.Long":
						Optional.ofNullable(resultSet.getLong(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.numberNode(data));
						});
						break;
					case "java.lang.Double":
						Optional.ofNullable(resultSet.getDouble(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.numberNode(data));
						});
						break;
					case "java.lang.Float":
						Optional.ofNullable(resultSet.getFloat(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.numberNode(data));
						});
						break;
					case "java.math.BigDecimal":
						Optional.ofNullable(resultSet.getBigDecimal(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.numberNode(data));
						});
						break;
					case "java.lang.Boolean":
						Optional.ofNullable(resultSet.getBoolean(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.booleanNode(data));
						});
						break;
					case "java.util.Date":
					case "java.sql.Date":
					case "java.time.LocalDateTime":
						Optional.ofNullable(resultSet.getTimestamp(columnIndex)).ifPresent(data -> {
							objectNode.set(columnName, jsonNodeFactory.textNode(simpleDateFormat.format(data)));
						});
						break;
					default:
						break;
					}
				}
				Object historyData = objectMapper.treeToValue(objectNode, Object.class);
				UpdateHistoryDocument updateHistoryDocument = new UpdateHistoryDocument();
				updateHistoryDocument.setHistoryData(historyData);
				updateHistoryDocument.setOperateTime(LocalDateTime.now());
				updateHistoryDocument.setOperateType(DeleteHistoryDocument.UPDATE_OPERATE);
				updateHistoryDocument.setOperator(getOperator());
				updateHistoryDocument.setTable(metaData.getTableName(1));
				mongoTemplate.save(updateHistoryDocument);
			}
		} catch (SQLException | JsonProcessingException | IllegalArgumentException e) {
			log.error("data dml history save to mongodb failed", e);
		}

	}
}
