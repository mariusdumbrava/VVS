package Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Socket;

import org.junit.jupiter.api.Test;

import main.Server;
public class SocketTest {
    Server server =new Server();

    @Test
    public void test_set_getSocketClient() {
        Socket sock = new Socket();
        server.setClientSocket(sock);
        assertEquals(sock,server.getClientSocket());
    }
}
