package liquibase.ext.ignite.database;

import java.util.List;
import liquibase.CatalogAndSchema;
import liquibase.Scope;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Catalog;
import liquibase.structure.core.Column;
import liquibase.structure.core.Index;
import liquibase.structure.core.Schema;
import liquibase.structure.core.Table;

public class IgniteDatabase extends AbstractJdbcDatabase {

  public static final String PRODUCT_NAME = "Apache Ignite";
  public static final String SHORT_PRODUCT_NAME = "Ignite";
  public static final int DEFAULT_PORT = 10800;
  public static final String DEFAULT_DRIVER = "org.apache.ignite.jdbc.IgniteJdbcDriver";
  private static final List<Class<? extends DatabaseObject>> supportedClasses = List.of(
      Catalog.class, Schema.class, Table.class, Column.class, Index.class
  );

  public IgniteDatabase() {
    defaultCatalogName = "IGNITE";
    currentDateTimeFunction = "CURRENT_TIMESTAMP";
    unquotedObjectsAreUppercased = true;
  }

  @Override
  protected String getDefaultDatabaseProductName() {
    return PRODUCT_NAME;
  }

  @Override
  public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
    var databaseProductName = conn.getDatabaseProductName();
    return PRODUCT_NAME.equalsIgnoreCase(databaseProductName);
  }

  @Override
  public String getDefaultDriver(String url) {
    return url.startsWith("jdbc:ignite:") ? DEFAULT_DRIVER : null;
  }

  @Override
  public String getShortName() {
    return SHORT_PRODUCT_NAME;
  }

  @Override
  public Integer getDefaultPort() {
    return DEFAULT_PORT;
  }

  @Override
  public boolean supportsInitiallyDeferrableColumns() {
    return false;
  }

  @Override
  public boolean supportsTablespaces() {
    return false;
  }

  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public boolean supportsDDLInTransaction() {
    return false;
  }

  @Override
  public String getSystemSchema() {
    return "SYSTEM";
  }

  @Override
  public boolean isSystemObject(final DatabaseObject example) {
    return example != null && example.getSchema() != null && example.getSchema().getName() != null
        && getSystemSchema().equalsIgnoreCase(example.getSchema().getName());
  }

  @Override
  public boolean isSystemView(CatalogAndSchema schema, String viewName) {
    return getSystemSchema().equalsIgnoreCase(schema.customize(this).getSchemaName());
  }

  @Override
  public boolean supportsSequences() {
    return false;
  }

  @Override
  public boolean supportsAutoIncrement() {
    return false;
  }

  @Override
  public void setConnection(DatabaseConnection conn) {
    super.setConnection(conn);
  }

  @Override
  protected String getConnectionSchemaName() {
    var connection = getConnection();
    if (connection == null) {
      return null;
    }

    try {
      return connection.getUnderlyingConnection().getSchema();
    } catch (Exception e) {
      Scope.getCurrentScope().getLog(getClass()).info("Error getting default schema", e);
    }
    return null;
  }

  @Override
  public String getDateLiteral(final String isoDate) {
    if (isDateOnly(isoDate) || isTimeOnly(isoDate)) {
      return "DATE'" + isoDate + "'";
    } else if (isDateTime(isoDate)) {
      return "'" + isoDate.replace('T', ' ') + "'";
    } else {
      return "BAD_DATE_FORMAT:" + isoDate;
    }
  }

  @Override
  public boolean supports(Class<? extends DatabaseObject> cls) {
    return supportedClasses.stream().anyMatch(c -> c.isAssignableFrom(cls));
  }

  @Override
  protected String getConnectionCatalogName() {
    return "IGNITE";
  }
}
