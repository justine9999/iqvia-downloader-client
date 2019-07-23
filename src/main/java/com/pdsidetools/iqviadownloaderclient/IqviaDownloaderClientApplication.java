package com.pdsidetools.iqviadownloaderclient;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pdsidetools.iqviadownloaderclient.service.IqviaDownloadService;

@SpringBootApplication
public class IqviaDownloaderClientApplication implements CommandLineRunner {

	@Autowired
	IqviaDownloadService iqviaDownloadService;
	
	public static void main(String[] args) {
		SpringApplication.run(IqviaDownloaderClientApplication.class, args);
		System.exit(0);
	}

	@Override
    public void run(String... args) {
		
        if(args.length < 2) {
        	System.out.println("argument number should be at least 2.");
        	return;
        }
        
        String id_str = args[0];
        String[] ids = Arrays.stream(id_str.split(",")).map(String::trim).toArray(String[]::new);
        if(ids == null || ids.length == 0){
        	System.out.println("please specify at least 1 submission id");
        	return;
        }
        
        File targetFolder = new File(args[1].trim());
        if(!targetFolder.exists() || !targetFolder.isDirectory()){
        	System.out.println("target folder path not exist or invalid.");
        	return;
        }
        
        try{
        	iqviaDownloadService.downloadIqvia(id_str, targetFolder.getAbsolutePath());
        }catch(Exception e){
        	System.out.println("error processing job.");
        	e.printStackTrace();
        	return;
        }
        
        System.out.println("job completed successfully.");
    }
}
