package com.nicoletti.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nicoletti.model.Arquivo;
import com.nicoletti.repository.FileRepository;

@RestController
@RequestMapping("/arquivos")
public class FileController {
	
	@Autowired
	private FileRepository fileRepository;
	
	@GetMapping("/download")
	public ResponseEntity<byte[]> download(@Param("id") Long id) throws Exception {
		
		
		Optional<Arquivo> result = fileRepository.findById(id);
		if (!result.isPresent()) {
			throw new Exception("Could not find file with ID: " + id);
		}
		
		Arquivo arquivo = result.get();
		byte[] bytes = arquivo.getContent();
		
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, arquivo.getContentType())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + arquivo.getName())
			.body(bytes);
	}
	
	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public Arquivo upload(@RequestParam("arquivo") MultipartFile arquivo) throws IOException {
		String fileName = StringUtils.cleanPath(arquivo.getOriginalFilename());
		
		Arquivo file = new Arquivo();
		file.setName(fileName);
		file.setContent(arquivo.getBytes());
		file.setSize(arquivo.getSize());
		file.setUploadTime(new Date());
		
		return fileRepository.save(file);
		
	}

}
