package com.caovy2001.data_everywhere.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileAPI {
    @GetMapping(value = "/", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody ResponseEntity<byte[]> downloadFile(@RequestParam String path) {
        try {
            if (StringUtils.isBlank(path)) {
                return null;
            }

            InputStream in = this.getClass().getResourceAsStream(path);
            if (in == null) {
                return null;
            }

            String name = path;
            if (path.contains("/")) {
                String[] splitPath = path.split("/");
                name = splitPath[splitPath.length-1];
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(in.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
