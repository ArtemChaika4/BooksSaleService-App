package ua.edu.dnu.booksales.util;

import ua.edu.dnu.booksales.dao.Service;

import java.sql.*;

public class BackupUtil {
    public static void saveToBackup(String path) throws SQLException {
        try (Connection connection = Service.getConnection()){
            String BACKUP_QUERY = "BACKUP DATABASE books_sale " +
                    "TO DISK = '" + path + "' " +
                    "WITH FORMAT, " +
                    "MEDIANAME = 'SQLServerBackups', " +
                    "NAME = 'Full Backup of books_sale'";
            Statement statement = connection.createStatement();
            statement.execute(BACKUP_QUERY);
        }
    }

    public static void restoreFromBackup(String path) throws SQLException {
        try (Connection connection = Service.getConnection()){
            String RESTORE_QUERY = "USE master; " +
                    "RESTORE DATABASE books_sale " +
                    "FROM DISK = '" + path + "'";
            Statement statement = connection.createStatement();
            statement.execute(RESTORE_QUERY);
        }
    }
}
