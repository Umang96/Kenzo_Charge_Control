package umang96.kenzochargecontrol;



        import java.io.BufferedReader;
        import java.io.InputStreamReader;

public class ShellExecutor {

    public ShellExecutor() {

    }

    public String Executor(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
          // p = Runtime.getRuntime().exec("su");
            p = Runtime.getRuntime().exec(command);
           // p.getOutputStream().write(command.getBytes());
            //p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;

    }
}