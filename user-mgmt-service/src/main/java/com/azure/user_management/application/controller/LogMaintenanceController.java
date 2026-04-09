package com.azure.user_management.application.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController
@RequestMapping("/mylogger")
@Slf4j
public class LogMaintenanceController {
    @Value("${logging.file.name}")
    private String logfilepath;

    @GetMapping("/TestAPI")
    public String test() {
        log.info("into TestAPI");
        log.warn("Wrning - into TestAPI");
        log.error("Error - into TestAPI");
        return "Hello World";
    }

    @GetMapping("/sec/process/get-files-list")
    public String getFiles() throws IOException {
        Path path = Paths.get(logfilepath);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return "No Records found";
        }

        List<String> files = new ArrayList<>();
        File currentLogFile = resource.getFile();
        Files.list(Paths.get(currentLogFile.getParentFile().toURI())).filter(s -> s.toString().endsWith(".gz")).sorted()
                .forEach(o -> {
                    System.out.println(o);
                    files.add(o.toString());
                });
        System.out.println("------------------------------------------------");
        Collections.reverse(files);
        StringBuffer sb = new StringBuffer();
        for(String filePath : files){
            sb.append("\n" + filePath);
        }
        if(sb.isEmpty()){
            sb.append("No Files found" );
        }
        return sb.toString();
    }

    @PostMapping("/sec/process/get-file")
    public ResponseEntity<String> getFile(@RequestBody String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String fileNameDecodedString = URLDecoder.decode(json, StandardCharsets.UTF_8.toString());
        Map<String,String> map = objectMapper.readValue(fileNameDecodedString,Map.class) ;

        String fileName = map.get("fileName");
        Path path = Paths.get(logfilepath);
        Resource resource = new UrlResource(path.toUri());
        File logFolder = resource.getFile().getParentFile();

        File fileReturnObj =  null;
        List<Path> files = new ArrayList<>();
        Files.list(Paths.get(logFolder.toURI())).filter(s -> s.toString().endsWith(".gz")).sorted()
                .forEach(o -> {
                    System.out.println(o);
                    files.add(o);
                });
         for(Path fileObj :  files)     {
             if(fileObj.toFile().getName().equals(fileName)){
                 fileReturnObj = fileObj.toFile();
                 break;
             }
         }
        HttpHeaders headers = new HttpHeaders();
        if (fileReturnObj != null && fileReturnObj.exists()) {
            String returnString = fileToBase64(fileReturnObj.getAbsolutePath());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(returnString);
        } else {
            return null;
        }
    }

    @GetMapping("/sec/process/get-running-file")
    public ResponseEntity<String> getRunningFile() throws Exception {

        Path path = Paths.get(logfilepath);
        Resource resource = new UrlResource(path.toUri());

        HttpHeaders headers = new HttpHeaders();
        File currentLogFile = resource.getFile();
        if (currentLogFile.exists()) {
            String returnString = fileToBase64(currentLogFile.getAbsolutePath());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(returnString);
        } else {
            return null;
        }

    }

    public static String fileToBase64(String filePath) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
