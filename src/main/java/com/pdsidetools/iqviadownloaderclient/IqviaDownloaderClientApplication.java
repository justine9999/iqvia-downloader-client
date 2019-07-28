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
        
        String id_str = null;
        File targetFolder = null;
        
        for(String arg : args){
        	String[] parts = arg.split("=");
        	if(parts.length != 2){
        		continue;
        	}
        	String name = parts[0].trim();
        	String value = parts[1].trim();
        	
        	if(name.equals("ids")){
        		id_str = value;
        	}else if(name.equals("target_folder")){
        		targetFolder = new File(value);
        	}
        }
        
        if(id_str == null){
        	System.out.println("submission id values not specified");
        	return;
        }
        String[] ids = Arrays.stream(id_str.split(",")).map(String::trim).toArray(String[]::new);
        if(ids == null || ids.length == 0){
        	System.out.println("please specify at least 1 submission id");
        	return;
        }
        
        if(targetFolder == null){
        	System.out.println("target folder values not specified");
        	return;
        }
        if(!targetFolder.exists() || !targetFolder.isDirectory()){
        	System.out.println("target folder path not exist or invalid: " + targetFolder.getAbsolutePath());
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
