package liquibase.ext.ignite.database;

import java.util.Set;
import liquibase.CatalogAndSchema;
import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;

public class IgniteDatabase extends AbstractJdbcDatabase {

  public static final String PRODUCT_NAME = "Apache Ignite";
  public static final String SHORT_PRODUCT_NAME = "ignite";
  public static final int DEFAULT_PORT = 10800;
  public static final String DEFAULT_DRIVER = "org.apache.ignite.jdbc.IgniteJdbcDriver";

  private final Set<String> systemViews = Set.of(
      "COMPUTE_TASKS",
      "GLOBAL_PARTITION_STATES",
      "GLOBAL_ZONE_PARTITION_STATES",
      "INDEXES",
      "INDEX_COLUMNS",
      "LOCAL_ZONE_PARTITION_STATES",
      "LOCKS",
      "LOCAL_PARTITION_STATES",
      "SCHEMAS",
      "SQL_QUERIES",
      "SQL_CACHED_QUERY_PLANS",
      "SYSTEM_VIEWS",
      "SYSTEM_VIEW_COLUMNS",
      "TABLES",
      "TABLE_COLUMNS",
      "TRANSACTIONS",
      "ZONES",
      "ZONE_STORAGE_PROFILES"
  );

  public IgniteDatabase() {
    defaultSchemaName = "PUBLIC";
    currentDateTimeFunction = "CURRENT_TIMESTAMP";
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
  public boolean isSystemView(CatalogAndSchema schema, String viewName) {
    return getSystemSchema().equalsIgnoreCase(schema.customize(this).getSchemaName())
        || systemViews.contains(viewName);
  }

  @Override
  protected Set<String> getSystemViews() {
    return systemViews;
  }
}
