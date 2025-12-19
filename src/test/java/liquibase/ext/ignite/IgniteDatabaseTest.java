package liquibase.ext.ignite;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

public class IgniteDatabaseTest extends AbstractIgniteTest {

  @Test
  void test1() throws Exception {
    try (var database = DatabaseFactory.getInstance()
        .openDatabase("jdbc:ignite:thin://127.0.0.1", null, null, null, null)) {
      var liquibase = new Liquibase("chinook/changelog.xml", new ClassLoaderResourceAccessor(), database);
      liquibase.update();
    }
  }
}
