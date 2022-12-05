package kr.saebyeok.santafy.util.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database implements AutoCloseable {

    public final Connection connection;

    public Database(File file) throws IOException, SQLException {
        if (!file.exists()) {
            file.mkdirs();
            file.createNewFile();
        }
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
        connection.setAutoCommit(true);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

}
