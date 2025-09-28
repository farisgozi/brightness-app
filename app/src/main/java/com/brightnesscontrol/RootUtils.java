package main.java.com.brightnesscontrol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RootUtils {
    private static final String TAG = "RootUtils";
    
    public boolean isRooted() {
        return executeCommand("id").contains("uid=0");
    }
    
    public String executeCommand(String command) {
        StringBuilder result = new StringBuilder();
        Process process = null;
        DataOutputStream os = null;
        BufferedReader reader = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
                if (reader != null) reader.close();
                if (process != null) process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return result.toString().trim();
    }
    
    public boolean executeCommands(List<String> commands) {
        Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            
            for (String command : commands) {
                os.writeBytes(command + "\n");
                os.flush();
            }
            
            os.writeBytes("exit\n");
            os.flush();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) os.close();
                if (process != null) process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public String readFile(String filePath) {
        return executeCommand("cat " + filePath);
    }
    
    public boolean writeFile(String filePath, String content) {
        List<String> commands = new ArrayList<>();
        commands.add("echo '" + content + "' > " + filePath);
        return executeCommands(commands);
    }
    
    public boolean fileExists(String filePath) {
        String result = executeCommand("test -f " + filePath + " && echo 'exists' || echo 'not found'");
        return result.contains("exists");
    }
    
    public boolean makeFileWritable(String filePath) {
        return executeCommands(List.of("chmod 666 " + filePath));
    }
}
