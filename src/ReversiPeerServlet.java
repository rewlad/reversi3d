import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.*;

public class ReversiPeerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException 
    {
        String create = req.getParameter("create");
        String peer = req.getParameter("peer");
        String state = req.getParameter("state");
        resp.setContentType("text/plain");
        try{
            if(create!=null){
                if(create.equals("uuid")){
                    resp.getWriter().print(UUID.randomUUID());
                }else{
                    resp.getWriter().print(ReversiChannelServlet.ChannelHub.createChannel(create));
                }
            }
            if(peer!=null){
                ReversiChannelServlet.ChannelHub.send(peer, state==null?"":state);
            }
        }catch(IllegalArgumentException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }
}
