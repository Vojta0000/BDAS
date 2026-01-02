package upce.javafx.utils;

import upce.javafx.ConnectionSingleton;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to run SQL scripts.
 */
public class ScriptRunner {

    /**
     * Executes an Oracle SQL script from the given path.
     * Supports both ';' and '/' (for PL/SQL blocks) as delimiters.
     *
     * @param scriptPath Path to the SQL script file.
     * @throws IOException  If the file cannot be read.
     * @throws SQLException If an error occurs during execution.
     */
    public void runScript(String scriptPath) throws IOException, SQLException {
        List<String> commands = parseScript(scriptPath);
        try (Connection conn = ConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement()) {
                for (String command : commands) {
                    if (command.trim().isEmpty()) continue;
                    try {
                        stmt.execute(command);
                    } catch (SQLException e) {
                        // For destroy script, we might want to ignore some errors,
                        // but generally we should report them.
                        System.err.println("Error executing command: " + command);
                        System.err.println("Error message: " + e.getMessage());
                        // If it's not the destroy script, we might want to rethrow.
                        // For simplicity, let's rethrow all unless we decide otherwise.
                        throw e;
                    }
                }
            }
        }
    }

    private List<String> parseScript(String scriptPath) throws IOException {
        List<String> commands = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                
                // Skip comments and empty lines
                if (trimmedLine.startsWith("--") || trimmedLine.isEmpty()) {
                    continue;
                }

                // Check for PL/SQL block terminator '/'
                if (trimmedLine.equals("/")) {
                    if (sb.length() > 0) {
                        commands.add(sb.toString().trim());
                        sb.setLength(0);
                    }
                    continue;
                }

                sb.append(line).append("\n");

                // Check for standard ';' terminator, but only if not inside a PL/SQL block
                if (trimmedLine.endsWith(";")) {
                    String currentContent = sb.toString().toUpperCase().trim();
                    if (!isInsidePlSql(currentContent)) {
                        String cmd = sb.toString().trim();
                        // Remove the trailing ';' for plain SQL statements
                        commands.add(cmd.substring(0, cmd.length() - 1));
                        sb.setLength(0);
                    }
                }
            }
            // Add remaining content if any
            if (sb.length() > 0) {
                commands.add(sb.toString().trim());
            }
        }
        return commands;
    }

    private boolean isInsidePlSql(String content) {
        // Very basic heuristic to detect if we are inside a PL/SQL block or DDL that needs '/'
        return content.contains("BEGIN") || 
               content.contains("DECLARE") || 
               content.contains("CREATE OR REPLACE FUNCTION") ||
               content.contains("CREATE OR REPLACE PROCEDURE") ||
               content.contains("CREATE OR REPLACE TRIGGER") ||
               content.contains("CREATE OR REPLACE TYPE") ||
               content.contains("CREATE FUNCTION") ||
               content.contains("CREATE PROCEDURE") ||
               content.contains("CREATE TRIGGER");
    }
}
