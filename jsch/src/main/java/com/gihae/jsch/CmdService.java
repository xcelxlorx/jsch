package com.gihae.jsch;

import com.gihae.jsch.exception.Exception;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class CmdService {

    @Value("${ec2.host}")
    private String host;

    @Value("${ec2.privateKey}")
    private String privateKey;

    private int port = 6001;

    public void create(String password){
        String key = generateKey();
        String command = createImgCmd("vncdesktop", key) + createContainerCmd(password, key) + stopContainerCmd(key);
        execute(command);
        port++;
    }

    public void access(String key){
        String command = startContainerCmd(key);
        execute(command);
    }

    public void save(String key){
        String command = commitContainerCmd(key) + saveImgCmd(key);
        execute(command);
    }

    public void load(String password, String findKey){
        String key = generateKey();
        String command = createImgCmd(findKey, key) + createContainerCmd(password, key) + stopContainerCmd(key);
        execute(command);
        port++;
    }

    public void delete(String key){
        String command = deleteImgCmd(key) + deleteContainerCmd(key);
        execute(command);
    }

    void execute(String command){
        try{
            // SSH 클라이언트 설정
            JSch jsch = new JSch();
            jsch.addIdentity(privateKey);

            // SSH 세션 열기
            Session session = jsch.getSession("ubuntu", host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 명령 실행을 위한 채널 열기
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

            // 명령어 실행 및 결과 처리
            channelExec.setCommand(command);
            InputStream commandOutput = channelExec.getInputStream();
            channelExec.connect();

            // 명령어 실행 결과를 읽어옴
            StringBuilder output = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = commandOutput.read(buffer)) > 0) {
                output.append(new String(buffer, 0, bytesRead));
            }

            // 채널 및 세션 닫기
            channelExec.disconnect();
            session.disconnect();

            log.info("Output:\n" + output.toString());
        }catch(JSchException | IOException e){
            throw new Exception.Exception500("Internal Server Error: " + e.getMessage());
        }
    }

    String generateKey(){
        return String.valueOf(port);
    }

    String createImgCmd(String fromKey, String toKey){
        return "docker tag registry.p2kcloud.com/base/" + fromKey + " registry.p2kcloud.com/base/" + toKey + "\n";
    }

    String saveImgCmd(String key){
        return "docker push registry.p2kcloud.com/base/" + key + "\n";
    }

    String deleteImgCmd(String key){
        return "docker rmi registry.p2kcloud.com/base/" + key + "\n";
    }

    String createContainerCmd(String password, String key){
        return "docker run -d -p " + port + ":6901 -e VNC_PW='" + password + "' --name " + key + " registry.p2kcloud.com/base/" + key + "\n";
    }

    String startContainerCmd(String key){
        return "docker start " + key + "\n";
    }

    String stopContainerCmd(String key){
        return "docker stop " + key + "\n";
    }

    String commitContainerCmd(String key){
        return "docker commit " + key + " registry.p2kcloud.com/base/" + key + "\n";
    }

    String deleteContainerCmd(String key){
        return "docker rm " + key + "\n";
    }

    String testCmd(){
        return "echo \"test!\"";
    }
}
