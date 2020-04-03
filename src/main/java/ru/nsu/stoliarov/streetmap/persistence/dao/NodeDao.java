package ru.nsu.stoliarov.streetmap.persistence.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.nsu.stoliarov.streetmap.model.Node;
import ru.nsu.stoliarov.streetmap.retention.model.RetentionOptions;
import ru.nsu.stoliarov.streetmap.persistence.config.PostgresConnectionConfig;
import ru.nsu.stoliarov.streetmap.persistence.util.DateTimeConverter;
import ru.nsu.stoliarov.streetmap.speedtest.SpeedTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeDao {

    private static final String STATEMENT_INSERT = "insert into osm.node values ";

    private static final String PREPARED_STATEMENT_INSERT = "insert into osm.node values (?,?,?,?,?,?,?,?)";

    private static final String DELETE_ALL = "delete from osm.node";

    private final PostgresConnectionConfig config;

    private final DateTimeConverter dateTimeConverter;

    private PreparedStatement batchStatement;

    private Connection batchConnection;

    private int batchCount;


    @SpeedTest
    public void save(Node node, RetentionOptions retentionOptions) {
        SaveMode saveMode = retentionOptions.getSaveMode();

        switch (saveMode) {
            case STATEMENT:
                saveUsingStatement(node);
                break;
            case PREPARED_STATEMENT:
                saveUsingPreparedStatement(node);
                break;
            case BATCH:
                saveUsingBatch(node, retentionOptions.getBatchSize());
                break;
            default:
                log.warn("Failed to save node: {}. Unknown save mode: {}", node, saveMode);
        }
    }

    @SpeedTest(isFlush = true)
    public void flush() {

        if (batchStatement == null) {
            return;
        }

        try {
            batchStatement.executeBatch();
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

    private void saveUsingStatement(Node node) {
        try {
            Connection connection =
                    DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

            Statement statement = connection.createStatement();

            String dateTime = dateTimeConverter.convertToPostgresString(node.getDateTime());

            if (StringUtils.isNotBlank(dateTime)) {
                dateTime = "'" + dateTime + "'";
            }

            String query = STATEMENT_INSERT + "(" +
                    node.getId() +
                    ",'" +
                    node.getVersion() +
                    "'," +
                    dateTime +
                    "," +
                    node.getUid() +
                    ", '" +
                    node.getUserName() +
                    "'," +
                    node.getChangeSet() +
                    "," +
                    node.getLat() +
                    "," +
                    node.getLon() +
                    ")";

            statement.executeUpdate(query);
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUsingPreparedStatement(Node node) {
        try {
            Connection connection =
                    DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

            PreparedStatement statement = connection.prepareStatement(PREPARED_STATEMENT_INSERT);

            ZonedDateTime dateTime = node.getDateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone(dateTime.getZone()));

            statement.setLong(1, node.getId());
            statement.setString(2, node.getVersion());
            statement.setTimestamp(3, dateTimeConverter.convertToTimestamp(dateTime), calendar);
            statement.setLong(4, node.getUid());
            statement.setString(5, node.getUserName());
            statement.setLong(6, node.getChangeSet());
            statement.setDouble(7, node.getLat());
            statement.setDouble(8, node.getLon());

            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUsingBatch(Node node, int batchSize) {
        try {

            if (this.batchStatement == null) {

                this.batchConnection =
                        DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());

                this.batchStatement = this.batchConnection.prepareStatement(PREPARED_STATEMENT_INSERT);
            }

            ZonedDateTime dateTime = node.getDateTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone(dateTime.getZone()));

            this.batchStatement.setLong(1, node.getId());
            this.batchStatement.setString(2, node.getVersion());
            this.batchStatement.setTimestamp(3, dateTimeConverter.convertToTimestamp(dateTime), calendar);
            this.batchStatement.setLong(4, node.getUid());
            this.batchStatement.setString(5, node.getUserName());
            this.batchStatement.setLong(6, node.getChangeSet());
            this.batchStatement.setDouble(7, node.getLat());
            this.batchStatement.setDouble(8, node.getLon());

            this.batchStatement.addBatch();

            if (++this.batchCount >= batchSize) {
                this.batchStatement.executeBatch();
                this.batchStatement.close();
                this.batchConnection.close();
                this.batchStatement = null;
                this.batchConnection = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
