
import java.io.IOException;
import javax.servlet.http.*;
import java.util.UUID;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

public class ReversiPeerServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException 
    {
        String create = req.getParameter("create");
        String peer = req.getParameter("peer");
        String state = req.getParameter("state");
        resp.setContentType("text/plain");
        ChannelService cs = ChannelServiceFactory.getChannelService();
        if(create!=null) resp.getWriter().print(
            create.equals("uuid") ? UUID.randomUUID() : cs.createChannel(create)
        );
        if(peer!=null) cs.sendMessage(new ChannelMessage(peer, state));
    }
}
