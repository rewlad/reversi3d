
import java.io.IOException;
import javax.servlet.http.*;
import java.math.BigInteger;

public class ReversiPlayServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException 
    {
        try{
            String pi = req.getPathInfo().substring(1,33);
            ReversiPlayer player = new ReversiPlayerShifter4();
            long bestmove = player.findBestMove(
                new BigInteger(pi.substring(0,16), 16).longValue(),
                new BigInteger(pi.substring(16,32), 16).longValue(),
                0L /*required by other player types*/
            );
            resp.setContentType("text/plain");
            resp.getWriter().printf("%016x",bestmove);
        }catch(Exception ex){
            throw new IOException(ex);
        }
    }
}
