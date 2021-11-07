package Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

import main.Server;
public class listenClientsTest {
    Server server = new Server();

    @Test
    public void testListen() throws IOException {
        server.setPort(1234);
        server.acceptServerPort();
        int connection=server.conectionClient;
        assertEquals(0,connection);

    }
}
