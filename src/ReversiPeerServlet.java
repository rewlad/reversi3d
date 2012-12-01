
import java.io.IOException;
import javax.servlet.http.*;

public class ReversiPeerServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException 
    {
        String pi = req.getPathInfo();
        ChannelService cs = ChannelServiceFactory.getChannelService();
        resp.setContentType("text/plain");
        resp.getWriter().write(cs.createChannel(pi));
    }
}

//cs.sendMessage(new ChannelMessage(channelKey, getMessageString()));
