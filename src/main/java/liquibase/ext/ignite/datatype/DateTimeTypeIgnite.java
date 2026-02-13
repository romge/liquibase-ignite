package liquibase.ext.ignite.datatype;

import java.util.Locale;
import liquibase.database.Database;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.datatype.core.DateTimeType;
import liquibase.ext.ignite.database.IgniteDatabase;
import org.apache.commons.lang3.StringUtils;

@DataTypeInfo(name = "datetime", minParameters = 0, maxParameters = 1,
    aliases = {"java.sql.Types.DATETIME", "java.util.Date", "smalldatetime", "datetime2"},
    priority = LiquibaseDataType.PRIORITY_DEFAULT + 1)
public class DateTimeTypeIgnite extends DateTimeType {

  @Override
  public boolean supports(Database database) {
    return database instanceof IgniteDatabase;
  }

  @Override
  public DatabaseDataType toDatabaseDataType(Database database) {
    if (database instanceof IgniteDatabase) {
      var rawDefinition = StringUtils.trimToEmpty(getRawDefinition()).toLowerCase(Locale.US);
      var params = getParameters();
      var param = switch (params.length) {
        case 0 -> "";
        case 1 -> "(" + params[0] + ")";
        default -> "(" + params[1] + ")";
      };
      return new DatabaseDataType(rawDefinition.contains("with local timezone")
          ? "TIMESTAMP" + param + " WITH LOCAL TIMEZONE"
          : "TIMESTAMP" + param);
    }
    return super.toDatabaseDataType(database);
  }
}
