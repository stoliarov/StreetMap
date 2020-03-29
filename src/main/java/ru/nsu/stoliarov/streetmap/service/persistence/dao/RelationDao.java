package ru.nsu.stoliarov.streetmap.service.persistence.dao;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Relation;
import ru.nsu.stoliarov.streetmap.model.RelationMember;
import ru.nsu.stoliarov.streetmap.service.persistence.config.PostgresConnectionConfig;
import ru.nsu.stoliarov.streetmap.service.persistence.util.DateTimeConverter;

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
public class RelationDao {

	private static final String RELATION_INSERT = "insert into osm.relation values (?,?,?,?,?,?)";

	private static final String RELATION_MEMBER_INSERT = "insert into osm.relation_member values (?,?,?,?)";

	private static final String RELATION_TAGS_INSERT = "insert into osm.relation_tag values (?,?,?,?)";

	private static final String DELETE_ALL = "delete from osm.relation";

    private final PostgresConnectionConfig config;

    private final DateTimeConverter dateTimeConverter;

	public void save(Relation relation) {
		try {
			Connection connection =
					DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

			saveRelation(relation, connection);
			saveRelationMembers(relation, connection);
			saveRelationTags(relation, connection);

		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	private void saveRelation(Relation relation, Connection connection) throws SQLException {

		PreparedStatement statement = connection.prepareStatement(RELATION_INSERT);

		ZonedDateTime dateTime = relation.getDateTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(dateTime.getZone()));

		statement.setLong(1, relation.getId());
		statement.setString(2, relation.getVersion());
		statement.setTimestamp(3, dateTimeConverter.convertToTimestamp(dateTime), calendar);
		statement.setLong(4, relation.getUid());
		statement.setString(5, relation.getUserName());
		statement.setLong(6, relation.getChangeSet());

		statement.executeUpdate();
	}

	private void saveRelationMembers(Relation relation, Connection connection) throws SQLException {

		List<RelationMember> members = relation.getMember();

		if (CollectionUtils.isEmpty(members)) {
			return;
		}

		PreparedStatement statement = connection.prepareStatement(RELATION_MEMBER_INSERT);

		for (RelationMember member : members) {

			statement.setLong(1, RandomUtils.nextLong());
			statement.setString(2, member.getType().getName());
			statement.setString(3, member.getRole().getName());
			statement.setLong(4, relation.getId());

			statement.addBatch();
		}

		statement.executeUpdate();
	}

	private void saveRelationTags(Relation relation, Connection connection) throws SQLException {

		List<Pair<String, String>> tags = relation.getTags();

		if (CollectionUtils.isEmpty(tags)) {
			return;
		}

		PreparedStatement statement = connection.prepareStatement(RELATION_TAGS_INSERT);

		for (Pair<String, String> tag : tags) {

			statement.setLong(1, RandomUtils.nextLong());
			statement.setString(2, tag.getKey());
			statement.setString(3, tag.getValue());
			statement.setLong(4, relation.getId());

			statement.addBatch();
		}

		statement.executeUpdate();
	}
}
