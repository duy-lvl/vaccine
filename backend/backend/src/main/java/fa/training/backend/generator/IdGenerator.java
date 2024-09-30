package fa.training.backend.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class IdGenerator implements IdentifierGenerator {

    protected String ID_PREFIX = "";
    private static final int ID_LENGTH = 6;
    protected String TABLE_NAME = "";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        final String[] generatedId = {null};

        session.doWork(connection -> {
            try {
                Statement statement = connection.createStatement();

                // Query to get the latest generated ID from your table
                String sql = "SELECT MAX(id) as max_id FROM " + TABLE_NAME;
                ResultSet rs = statement.executeQuery(sql);

                if (rs.next()) {
                    String lastId = rs.getString("max_id");
                    if (lastId != null) {
                        // Extract numeric part, increment it and add leading zeros
                        int lastNumericId = Integer.parseInt(lastId.substring(ID_PREFIX.length()));
                        int newNumericId = lastNumericId + 1;
                        generatedId[0] = ID_PREFIX + String.format("%0" + ID_LENGTH + "d", newNumericId);
                    }
                }

                // If no ID is found, start with 0000001
                if (generatedId[0] == null) {
                    generatedId[0] = ID_PREFIX + String.format("%0" + ID_LENGTH + "d", 1);
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error generating custom ID", e);
            }
        });

        return generatedId[0];
    }
}