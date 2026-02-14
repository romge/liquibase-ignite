package liquibase.ext.ignite.snapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.diff.compare.DatabaseObjectComparatorFactory;
import liquibase.exception.DatabaseException;
import liquibase.executor.ExecutorService;
import liquibase.ext.ignite.database.IgniteDatabase;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.IndexSnapshotGenerator;
import liquibase.statement.core.RawParameterizedSqlStatement;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Catalog;
import liquibase.structure.core.Column;
import liquibase.structure.core.Index;
import liquibase.structure.core.Schema;
import liquibase.structure.core.Table;

public class IndexSnapshotGeneratorIgnite extends IndexSnapshotGenerator {

  @Override
  public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
    return database instanceof IgniteDatabase ? super.getPriority(objectType, database) : PRIORITY_NONE;
  }

  @Override
  protected void addTo(DatabaseObject foundObject, DatabaseSnapshot snapshot) throws DatabaseException {
    if (!snapshot.getSnapshotControl().shouldInclude(Index.class) || !snapshot.getDatabase().supports(Index.class)) {
      return;
    }

    if (foundObject instanceof Table table) {
      var database = snapshot.getDatabase();
      var schema = table.getSchema();

      try {
        var query = "SELECT INDEX_NAME, IS_UNIQUE_INDEX, INDEX_TYPE, INDEX_COLUMNS FROM SYSTEM.INDEXES WHERE " +
            "SCHEMA_NAME = ? AND TABLE_NAME = ?";
        var executor = Scope.getCurrentScope().getSingleton(ExecutorService.class).getExecutor("jdbc", database);
        var rs = executor.queryForList(new RawParameterizedSqlStatement(query, schema.getName(), table.getName()));

        for (var row : rs) {
          table.getIndexes().add(readIndex(row));
        }
      } catch (Exception e) {
        throw new DatabaseException(e);
      }
    }
  }

  @Override
  @SuppressWarnings("java:S5411")
  protected DatabaseObject snapshotObject(DatabaseObject example, DatabaseSnapshot snapshot) throws DatabaseException {
    var database = snapshot.getDatabase();
    var exampleIndex = (Index) example;
    var exampleRelation = exampleIndex.getRelation();

    var schema = exampleRelation != null && exampleRelation.getSchema() != null ? exampleRelation.getSchema()
        : new Schema(database.getDefaultCatalogName(), database.getDefaultSchemaName());

    var exampleName = example.getName() != null ? database.correctObjectName(example.getName(), Index.class) : null;

    var foundIndexes = new HashMap<String, Index>();
    try {
      var query = "SELECT INDEX_NAME, TABLE_NAME, SCHEMA_NAME, IS_UNIQUE_INDEX, INDEX_TYPE, INDEX_COLUMNS FROM SYSTEM" +
          ".INDEXES " +
          "WHERE SCHEMA_NAME = ?";
      var parameters = new ArrayList<>();
      parameters.add(schema.getName());
      if (exampleRelation != null && exampleRelation.getName() != null) {
        query += " AND TABLE_NAME = ?";
        parameters.add(exampleRelation.getName());
      }
      if (exampleName != null) {
        query += " AND INDEX_NAME = ?";
        parameters.add(exampleName);
      }
      var executor = Scope.getCurrentScope().getSingleton(ExecutorService.class).getExecutor("jdbc", database);
      var rs = executor.queryForList(new RawParameterizedSqlStatement(query, parameters.toArray()));

      for (var row : rs) {
        var index = readIndex(row);
        if (exampleName != null) {
          return index;
        }
        foundIndexes.put(index.getName(), index);
      }

    } catch (Exception e) {
      throw new DatabaseException(e);
    }

    for (var index : foundIndexes.values()) {
      if (DatabaseObjectComparatorFactory.getInstance().isSameObject(index.getRelation(), exampleRelation,
          snapshot.getSchemaComparisons(), database)) {
        if (index.getColumnNames().equals(((Index) example).getColumnNames())) {
          return index;
        }
      }
    }
    return null;
  }

  private Index readIndex(Map<String, ?> indexMap) {
    var index = new Index((String) indexMap.get("INDEX_NAME"))
        .setRelation(new Table().setName((String) indexMap.get("TABLE_NAME"))
            .setSchema(new Schema((Catalog) null, (String) indexMap.get("SCHEMA_NAME"))))
        .setUnique((Boolean) indexMap.get("IS_UNIQUE_INDEX"))
        .setUsing((String) indexMap.get("INDEX_TYPE"));
    for (var columnName : ((String) indexMap.get("INDEX_COLUMNS")).split(", ")) {
      var column = new Column();
      if (columnName.endsWith(" ASC")) {
        column.setName(columnName.substring(0, columnName.length() - 4)).setDescending(false);
      } else if (columnName.endsWith(" DESC")) {
        column.setName(columnName.substring(0, columnName.length() - 5)).setDescending(true);
      } else {
        column.setName(columnName);
      }
      index.addColumn(column.setComputed(false).setRelation(index.getRelation()));
    }
    return index;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Class<? extends SnapshotGenerator>[] replaces() {
    return new Class[]{IndexSnapshotGenerator.class};
  }
}
