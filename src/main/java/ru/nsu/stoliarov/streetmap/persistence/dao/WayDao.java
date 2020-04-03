package ru.nsu.stoliarov.streetmap.persistence.dao;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Way;
import ru.nsu.stoliarov.streetmap.persistence.config.PostgresConnectionConfig;
import ru.nsu.stoliarov.streetmap.persistence.util.DateTimeConverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@Service
@AllArgsConstructor
public class WayDao {

	private static final String WAY_INSERT = "insert into osm.way values (?,?,?,?,?,?)";

	private static final String WAY_NODES_INSERT = "insert into osm.node_of_way values (?,?,?,?)";

	private static final String WAY_TAGS_INSERT = "insert into osm.way_tag values (?,?,?,?)";

	private static final String DELETE_ALL = "delete from osm.way";

    private final PostgresConnectionConfig config;

    private final DateTimeConverter dateTimeConverter;

    public void save(Way way) {
		try {
			Connection connection = 
					DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

			saveWay(way, connection);
			saveWayNodes(way, connection);
			saveWayTags(way, connection);

		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	private void saveWay(Way way, Connection connection) throws SQLException {

		PreparedStatement statement = connection.prepareStatement(WAY_INSERT);

		ZonedDateTime dateTime = way.getDateTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(dateTime.getZone()));

		statement.setLong(1, way.getId());
		statement.setString(2, way.getVersion());
		statement.setTimestamp(3, dateTimeConverter.convertToTimestamp(dateTime), calendar);
		statement.setLong(4, way.getUid());
		statement.setString(5, way.getUserName());
		statement.setLong(6, way.getChangeSet());

		statement.executeUpdate();
	}

	public void deleteAll() {
		try {
			Connection connection =
					DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

			Statement statement = connection.createStatement();
			statement.executeUpdate(DELETE_ALL);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveWayNodes(Way way, Connection connection) throws SQLException {

		List<Long> nodeIds = way.getNodeIds();

		if (CollectionUtils.isEmpty(nodeIds)) {
			return;
		}

		PreparedStatement statement = connection.prepareStatement(WAY_NODES_INSERT);

		for (int i = 0; i < nodeIds.size(); i++) {

			Long nodeId = nodeIds.get(i);

			statement.setLong(1, RandomUtils.nextLong());
			statement.setLong(2, way.getId());
			statement.setLong(3, nodeId);
			statement.setLong(4, i);

			statement.addBatch();
		}

		statement.executeBatch();
	}

	private void saveWayTags(Way way, Connection connection) throws SQLException {

		List<Pair<String, String>> tags = way.getTags();

		if (CollectionUtils.isEmpty(tags)) {
			return;
		}

		PreparedStatement statement = connection.prepareStatement(WAY_TAGS_INSERT);

		for (Pair<String, String> tag : tags) {

			statement.setLong(1, RandomUtils.nextLong());
			statement.setString(2, tag.getKey());
			statement.setString(3, tag.getValue());
			statement.setLong(4, way.getId());

			statement.addBatch();
		}

		statement.executeBatch();
	}
}
