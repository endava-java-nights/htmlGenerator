package htmlGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.html.HTMLElement;

public class HtmlGenerator {

    public static void run(Object root) {
        
        String html;
        try {
            html = render(root);
        } catch (Exception e1) {
            System.out.println("Error rendering model due to:" + e1);
            html = "<h1>Error rendering model!</h1>";
        }
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            Socket clientSocket = null;
            System.out.println("Open your browser on http://localhost:8080");
            
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    if (clientSocket != null)
                        System.out.println("Connected");

                } catch (IOException e) {
                    System.err.println("Accept failed.");
                    System.exit(1);
                }

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println("\r\n");
                System.out.println("html is:" + html);
                out.print(html);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8080.");
            System.exit(1);
        }
        
    }

    private static String render(Object root) throws Exception{
        StringBuilder sb = new StringBuilder();
        Class rootClass = root.getClass();
        Field[] fields = rootClass.getDeclaredFields();
        System.out.println("processing " + root);

        HtmlElement elem = root.getClass().getAnnotation(HtmlElement.class);
        String children = renderFields(root);
        if(!elem.type().equals(HTMLType.ROOT)) {
            String html = elem.type().getHtml(children);
            sb.append(html);
        }else {
            sb.append(children);
        }
        return sb.toString();
    }
    
    private static String renderFields(Object element) throws Exception{
        Field[] fields = element.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for(Field field : fields) {
            if(field.isAnnotationPresent(HtmlElement.class)) {
                field.setAccessible(true);
                Object fieldInstance = field.get(element);
                
                if(Collection.class.isAssignableFrom(fieldInstance.getClass())){
                    Collection collection = (Collection) fieldInstance;
                
                    Iterator iterator = collection.iterator();
                    StringBuilder children = new StringBuilder();
                    while(iterator.hasNext()) {
                        Object object = iterator.next();
                        children.append(render(object));
                    }
                    
                    HtmlElement elem = field.getAnnotation(HtmlElement.class);
                    String html = elem.type().getHtml(children.toString());
                    sb.append(html);                   
                }else {
                    HtmlElement elem = field.getAnnotation(HtmlElement.class);
                    String html = elem.type().getHtml(fieldInstance.toString());
                    sb.append(html);
                }
            }
        }
        return sb.toString();
    }
    
}
