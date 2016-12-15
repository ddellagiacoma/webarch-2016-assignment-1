package tinyhttpd;

import java.io.*;

/**
 *
 * @author Daniele Dellagiacoma
 */
public class Writer {

    //response from file request
    public static void writeFile(String req, OutputStream out) {
        try {
            FileInputStream fis = new FileInputStream(req);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            out.write(data);
        } catch (FileNotFoundException e) {
            new PrintStream(out).println("404 Not Found");
            System.out.println("404 Not Found: " + req);
        } catch (IOException e) {
            System.out.println("Generic I/O error on file " + e);
        }
    }

    //response from external process request
    public static void writeProcess(String req, OutputStream out) throws IOException {
        try {
            String line;

            InputStream stderr = null;
            InputStream stdout = null;
               
            //run the process, passing the parameters
            String[] parameters = req.split("\\?");
            Process process = Runtime.getRuntime().exec("java -jar " + parameters[0] + ".jar " + parameters[1]);

            stderr = process.getErrorStream();
            stdout = process.getInputStream();

            //catch the output of the process
            BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
            while ((line = brCleanUp.readLine()) != null) {
                new PrintStream(out).println(line);
            }
            brCleanUp.close();

            // clean up if any output in stderr and catch the error of the process
            brCleanUp = new BufferedReader(new InputStreamReader(stderr));
            while ((line = brCleanUp.readLine()) != null) {
                new PrintStream(out).println(line);
            }
            brCleanUp.close();
            
        } catch (ArrayIndexOutOfBoundsException e) {
            new PrintStream(out).println("The process URL is not correct");
            System.out.println("The process URL is not correct");
        } catch (IOException e) {
            System.out.println("Generic I/O error " + e);
        }
    }
}