package liquibase.ext.ignite.snapshot;

import liquibase.database.Database;
import liquibase.ext.ignite.database.IgniteDatabase;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotGenerator;
import liquibase.snapshot.jvm.UniqueConstraintSnapshotGenerator;
import liquibase.structure.DatabaseObject;


public class UniqueConstraintSnapshotGeneratorIgnite extends UniqueConstraintSnapshotGenerator {

  @Override
  public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
    return database instanceof IgniteDatabase ? PRIORITY_DATABASE : PRIORITY_NONE;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<? extends SnapshotGenerator>[] replaces() {
    return new Class[]{UniqueConstraintSnapshotGenerator.class};
  }

  @Override
  protected void addTo(DatabaseObject foundObject, DatabaseSnapshot snapshot) {}

  @Override
  protected DatabaseObject snapshotObject(DatabaseObject example, DatabaseSnapshot snapshot) {
    return null;
  }
}