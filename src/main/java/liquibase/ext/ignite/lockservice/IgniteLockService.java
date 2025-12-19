package liquibase.ext.ignite.lockservice;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.executor.ExecutorService;
import liquibase.ext.ignite.database.IgniteDatabase;
import liquibase.lockservice.StandardLockService;
import liquibase.statement.core.RawParameterizedSqlStatement;

public class IgniteLockService extends StandardLockService {

  @Override
  public int getPriority() {
    return PRIORITY_DATABASE;
  }

  @Override
  public boolean supports(Database database) {
    return database instanceof IgniteDatabase;
  }

  @Override
  protected boolean isDatabaseChangeLogLockTableCreated(boolean forceRecheck) {
    try {
      String sql = "select COUNT(*) from SYSTEM.TABLES where SCHEMA_NAME = ? and TABLE_NAME = ?;";
      var count = Scope.getCurrentScope().getSingleton(ExecutorService.class)
          .getExecutor("jdbc", database)
          .queryForInt(new RawParameterizedSqlStatement(sql, database.getLiquibaseSchemaName(),
              database.getDatabaseChangeLogLockTableName()));
      return count > 0;
    } catch (LiquibaseException e) {
      throw new UnexpectedLiquibaseException(e);
    }
  }
}
