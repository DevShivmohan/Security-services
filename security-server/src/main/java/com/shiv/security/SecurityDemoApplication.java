package com.shiv.security;

import com.shiv.security.constant.ApiConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@SpringBootApplication
public class SecurityDemoApplication {
	@Autowired
	private List<String> secretKeys;


	public static void main(String[] args) {
	//	killProcessViaPort(":8091",false);
		SpringApplication.run(SecurityDemoApplication.class, args);
	}

	@PostConstruct
	public void deleteAutoUploadedFiles(){
		var rootFile=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
		new Thread(()->{
			while (true){
				try {
//					deleteFiles(rootFile);
					deleteFilesUnReceived(rootFile);
					TimeUnit.MINUTES.sleep(30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * delete all files which are received
	 * @param rootFile
	 */
	private void deleteFiles(File rootFile){
		Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).forEach(file -> {
			if(file!=null && file.isDirectory())
				Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(file1 -> file1.isFile() && file1.getName().length()<=36)
						.toList().forEach(File::delete);
		});
	}

	/**
	 * delete all files which are unreceived and older than 1 days
	 * @param rootFile
	 */
	private void deleteFilesUnReceived(File rootFile){
		Arrays.stream(Objects.requireNonNull(rootFile.listFiles())).forEach(file -> {
			if(file!=null && file.isDirectory())
				Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(file1 -> file1.isFile() &&
								new Date(file1.lastModified()+TimeUnit.DAYS.toMillis(1)).before(new Date()))
						.toList().forEach(file2 ->{
							secretKeys.remove(file2.getName().substring(0,6));
							log.info("Deleting file - "+file2.getAbsolutePath()+" , delete status-"+file2.delete());
						});
		});
	}

	/**
	 * kill process which can be started port 8080
	 */
	public static void killProcessViaPort(String port,boolean isToDetectPort){
		if(isToDetectPort){
			var detectedPort=new StringBuilder();
			for(char ch:port.substring(port.lastIndexOf("port(s):"),port.lastIndexOf("(http) with context")).toCharArray())
				if((""+ch).matches("[0-9]+"))
					detectedPort.append(ch);
			port=":"+ detectedPort;
		}
		try {
			String pid=null;
			Process process=Runtime.getRuntime().exec("netstat -ltnup | grep "+port);
			var scanner=new Scanner(process.getInputStream());
			while (scanner.hasNext()){
				var readLine=scanner.nextLine();
				System.out.println(readLine);
				if(readLine.contains(port)){
					StringBuilder stringBuilder=new StringBuilder();
					for(char ch:readLine.substring(readLine.indexOf("LISTEN      "),readLine.lastIndexOf("/java")).toCharArray())
						if((""+ch).matches("[0-9]+"))
							stringBuilder.append(ch);
					pid=stringBuilder.toString();
					scanner.close();
					break;
				}
			}
			if(pid!=null && !pid.isBlank()){
				log.info("killing process with PID "+pid);
				process=Runtime.getRuntime().exec("kill -SIGKILL "+pid);
				process.waitFor();
				log.info("Process killed with PID "+pid);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
