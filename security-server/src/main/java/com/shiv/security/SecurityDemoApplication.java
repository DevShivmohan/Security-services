package com.shiv.security;

import com.shiv.security.constant.ApiConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootApplication
public class SecurityDemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(SecurityDemoApplication.class, args);
	}

	@PostConstruct
	private void deleteAutoUploadedFiles(){
		var rootFile=new File(ApiConstant.SERVER_DOWNLOAD_DIR);
		new Thread(()->{
			while (true){
				try {
					deleteFiles(rootFile);
					TimeUnit.HOURS.sleep(3);
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
		Arrays.stream(rootFile.listFiles()).forEach(file -> {
			if(file!=null && file.isDirectory())
				Arrays.stream(file.listFiles()).filter(file1 -> file1.isFile() && file1.getName().length()<=36)
						.collect(Collectors.toList()).forEach(file2 -> file2.delete());
		});
	}
}
