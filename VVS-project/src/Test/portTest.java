package Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.Server;
public class portTest {
    Server server;

    @Before
    public void setup() {
        server=new Server();
    }


    @Test
    public void testPortUnderZero() {
        assertEquals(false,server.setPort(-32));
    }

    @Test
    public void testPortAboveMax() {
        assertEquals(false,server.setPort(1003013));
    }


    @Test
    public void testSetPortZero() {
        int port=0;
        server.setPort(port);
        assertEquals(false,server.acceptServerPort());
    }




    @Test
    public void testAcceptServerPort()
    {
        server.setPort(8888);
        assertEquals(true,server.acceptServerPort());
    }

    @Test
    public void testNotAcceptServerPort()
    {
        server.setPort(10001);
        assertEquals(false,server.acceptServerPort());
    }

    @Test
    public void testNotAcceptServerPort2()
    {
        server.setPort(-10);
        assertEquals(false,server.acceptServerPort());
    }

}
