import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReversiChannelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final long POLL_TIMEOUT_MS = 25_000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        String token = req.getParameter("token");
        try{
            String message = ChannelHub.poll(token, POLL_TIMEOUT_MS);
            if(message==null){
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            resp.setContentType("text/plain");
            resp.getWriter().print(message);
        }catch(IllegalArgumentException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }catch(InterruptedException ex){
            Thread.currentThread().interrupt();
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    static final class ChannelHub {
        private static final Map<String, BlockingQueue<String>> CHANNELS =
            new ConcurrentHashMap<>();
        private static final Map<String, String> TOKENS =
            new ConcurrentHashMap<>();

        private ChannelHub() {}

        static String createChannel(String channelId){
            if(channelId==null || channelId.isEmpty()){
                throw new IllegalArgumentException("channelId");
            }
            CHANNELS.computeIfAbsent(channelId, id -> new LinkedBlockingQueue<>());
            String token = channelId + "-" + UUID.randomUUID().toString().replace("-", "");
            TOKENS.put(token, channelId);
            return token;
        }

        static void send(String channelId, String message){
            if(channelId==null || channelId.isEmpty()){
                throw new IllegalArgumentException("channelId");
            }
            BlockingQueue<String> queue = CHANNELS.computeIfAbsent(channelId, id -> new LinkedBlockingQueue<>());
            queue.offer(message==null ? "" : message);
        }

        static String poll(String token, long timeoutMs) throws InterruptedException {
            if(token==null || token.isEmpty()){
                throw new IllegalArgumentException("token");
            }
            String channelId = TOKENS.get(token);
            if(channelId==null){
                throw new IllegalArgumentException("token");
            }
            BlockingQueue<String> queue = CHANNELS.get(channelId);
            if(queue==null){
                throw new IllegalArgumentException("channelId");
            }
            return timeoutMs<=0 ? queue.poll() : queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        }
    }
}
