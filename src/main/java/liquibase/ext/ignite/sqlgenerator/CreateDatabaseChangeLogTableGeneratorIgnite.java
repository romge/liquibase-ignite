package liquibase.ext.ignite.sqlgenerator;

import liquibase.database.Database;
import liquibase.datatype.DataTypeFactory;
import liquibase.ext.ignite.database.IgniteDatabase;
import liquibase.sqlgenerator.core.CreateDatabaseChangeLogTableGenerator;
import liquibase.statement.NotNullConstraint;
import liquibase.statement.core.CreateDatabaseChangeLogTableStatement;
import liquibase.statement.core.CreateTableStatement;

public class CreateDatabaseChangeLogTableGeneratorIgnite extends CreateDatabaseChangeLogTableGenerator {

  @Override
  public boolean supports(CreateDatabaseChangeLogTableStatement statement, Database database) {
    return database instanceof IgniteDatabase;
  }

  protected CreateTableStatement getCreateTableStatement(Database database) {
    String charTypeName = getCharTypeName(database);
    String dateTimeTypeString = getDateTimeTypeString(database);
    String tableSpace = database.getLiquibaseTablespaceName();
    return new CreateTableStatement(database.getLiquibaseCatalogName(), database.getLiquibaseSchemaName(), database.getDatabaseChangeLogTableName())
        .setTablespace(tableSpace)
        .addPrimaryKeyColumn("ID", DataTypeFactory.getInstance().fromDescription(charTypeName + "(" + getIdColumnSize() + ")", database), null, null, tableSpace)
        .addPrimaryKeyColumn("AUTHOR", DataTypeFactory.getInstance().fromDescription(charTypeName + "(" + getAuthorColumnSize() + ")", database), null, null, tableSpace)
        .addPrimaryKeyColumn("FILENAME", DataTypeFactory.getInstance().fromDescription(charTypeName + "(" + getFilenameColumnSize() + ")", database), null, null, tableSpace)
        .addColumn("DATEEXECUTED", DataTypeFactory.getInstance().fromDescription(dateTimeTypeString, database), null, null, new NotNullConstraint())
        .addColumn("ORDEREXECUTED", DataTypeFactory.getInstance().fromDescription("int", database), null, null, new NotNullConstraint())
        .addColumn("EXECTYPE", DataTypeFactory.getInstance().fromDescription(charTypeName + "(10)", database), null, null, new NotNullConstraint())
        .addColumn("MD5SUM", DataTypeFactory.getInstance().fromDescription(charTypeName + "(35)", database))
        .addColumn("DESCRIPTION", DataTypeFactory.getInstance().fromDescription(charTypeName + "(255)", database))
        .addColumn("COMMENTS", DataTypeFactory.getInstance().fromDescription(charTypeName + "(255)", database))
        .addColumn("TAG", DataTypeFactory.getInstance().fromDescription(charTypeName + "(255)", database))
        .addColumn("LIQUIBASE", DataTypeFactory.getInstance().fromDescription(charTypeName + "(20)", database))
        .addColumn("CONTEXTS", DataTypeFactory.getInstance().fromDescription(charTypeName + "("+getContextsSize()+")", database))
        .addColumn("LABELS", DataTypeFactory.getInstance().fromDescription(charTypeName + "("+getLabelsSize()+")", database))
        .addColumn("DEPLOYMENT_ID", DataTypeFactory.getInstance().fromDescription(charTypeName+"(10)", database));
  }
}
