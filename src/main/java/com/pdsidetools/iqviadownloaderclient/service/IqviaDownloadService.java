package com.pdsidetools.iqviadownloaderclient.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IqviaDownloadService {

	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${iqvia-downloader-client.baseurl}")
	String serviceBaseUrl;
	
	public void downloadIqvia(String id_str, String targetFolder) throws Exception {
		
		String endpoint_get = serviceBaseUrl + "report";
		
		System.out.println("processing...");
		System.out.println("ids: " + id_str);
		System.out.println("target folder: " + targetFolder);
		System.out.println("service endpoint: " + endpoint_get);

		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());    
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<String>(id_str, headers);

		ResponseEntity<byte[]> response = restTemplate.exchange(endpoint_get, HttpMethod.POST, entity, byte[].class);
		String fileName = response.getHeaders().getContentDisposition().getFilename();
		String filePath = targetFolder + File.separator + fileName;

		if(response.getStatusCode().equals(HttpStatus.OK)){
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(new File(filePath));
				IOUtils.write(response.getBody(), output);
			}catch (Exception e) {
				throw e;
			}finally{
				output.close();
			}
			
		}else {
			throw new Exception("error downloading file, http status code: " + response.getStatusCode());
		}
		
		//delete request to delete remote job folder
		try {
			RestTemplate rt = new RestTemplate();
			String jobId = fileName.substring(fileName.lastIndexOf('_')+1, fileName.lastIndexOf('.'));
			String endpoint_delete = serviceBaseUrl + "jobs/" + jobId;
			
			System.out.println("deleting...");
			System.out.println("job id: " + jobId);
			System.out.println("service endpoint: " + endpoint_delete);
			
			rt.delete(endpoint_delete);
			
		} catch(Exception e){
			System.out.println("remote folder was not deleted.");
		}
	}
}
