package liquibase.ext.ignite;

import java.nio.file.Path;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServer;
import org.apache.ignite.InitParameters;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class AbstractIgniteTest {

  protected static IgniteServer node;
  protected static Ignite ignite;

  @BeforeAll
  public synchronized static void beforeAll() throws Exception {
    if (node == null) {
      SLF4JBridgeHandler.removeHandlersForRootLogger();
      SLF4JBridgeHandler.install();

      var igniteHome = Path.of(System.getProperty("user.dir"), "target/ignite");
      FileUtils.deleteDirectory(igniteHome.toFile());
      node = IgniteServer.start("node",
          Path.of(Objects.requireNonNull(
              AbstractIgniteTest.class.getResource("/ignite-config.conf")).toURI()),
          igniteHome);
      node.initCluster(InitParameters.builder()
          .metaStorageNodeNames("node")
          .clusterName("cluster")
          .build());
      ignite = node.api();
    }
  }
}
