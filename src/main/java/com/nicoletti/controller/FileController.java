package com.nicoletti.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.apache.commons.io.FileUtils;

import com.nicoletti.model.Arquivo;
import com.nicoletti.repository.FileRepository;

@Controller
public class FileController {
		
	@Autowired
	private FileRepository fileRepository;
	
	@GetMapping("/")
	public String viewHomePage(Model model) {
		List<Arquivo> listArquivos = fileRepository.findAll();
		model.addAttribute("listArquivos",listArquivos);
		
		List<Arquivo> listArquivosFileServer = new ArrayList<Arquivo>();
		File file = new File("C:\\temp");
		File[] afile = file.listFiles();
		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			Arquivo arq = new Arquivo((long) 0,arquivos.getName(),0);
			listArquivosFileServer.add(arq);
		}
		model.addAttribute("listArquivosFileServer",listArquivosFileServer);
		
		return "index";
	}
	
	
	@GetMapping("/download")
	public HttpEntity<byte[]> download(@Param("id") Long id) throws Exception {
		
		
		Optional<Arquivo> result = fileRepository.findById(id);
		if (!result.isPresent()) {
			throw new Exception("Could not find file with ID: " + id);
		}
		
		Arquivo arquivo = result.get();
		byte[] bytes = arquivo.getContent();
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Content-Disposition", "attachment;filename=\""+ arquivo.getName() +"\"");
		
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>( bytes, httpHeaders);
		
		return entity;
	}
	
	@PostMapping("/upload")
	public String upload(@RequestParam("arquivo") MultipartFile arquivo, 
			RedirectAttributes ra) throws Exception {
		
		String fileName = StringUtils.cleanPath(arquivo.getOriginalFilename());
				
		String extension = fileName.substring(fileName.length()-3);
						
		if (extension.compareTo("bat") == 0 || extension.compareTo("exe") == 0) {
			throw new Exception("Extenção de arquivo inválida: " + extension);
		}
		
		if (arquivo.getSize() > (10 * 1024 * 1024)) {
			throw new Exception("Tamanho de arquivo inválido. Permitido até 10Mb");
		}
		
		Arquivo file = new Arquivo();
		file.setName(fileName);
		file.setContent(arquivo.getBytes());
		file.setSize(arquivo.getSize());
		file.setUploadTime(new Date());
		
		fileRepository.save(file);
		
		ra.addFlashAttribute("message","Upload de arquivo realizado com sucesso!");
		
		return "redirect:/";
		
	}
	
	@GetMapping("/downloadFileServer")
	public HttpEntity<byte[]> downloadFileServer(@Param("name") String name) throws Exception {
		
		
		byte[] bytes = Files.readAllBytes( Paths.get("C:\\temp\\"+name) );
		
		HttpHeaders httpHeaders = new HttpHeaders();
		
		httpHeaders.add("Content-Disposition", "attachment;filename=\""+ StringUtils.cleanPath(name) +"\"");
		
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>( bytes, httpHeaders);
		
		return entity;
	}
	
	@PostMapping("/uploadFileServer")
	public String uploadFileServer(@RequestParam("arquivo") MultipartFile arquivo, 
			RedirectAttributes ra) throws Exception {
		
		String fileName = StringUtils.cleanPath(arquivo.getOriginalFilename());
				
		String extension = fileName.substring(fileName.length()-3);
						
		if (extension.compareTo("bat") == 0 || extension.compareTo("exe") == 0) {
			throw new Exception("Extenção de arquivo inválida: " + extension);
		}
		
		if (arquivo.getSize() > (10 * 1024 * 1024)) {
			throw new Exception("Tamanho de arquivo inválido. Permitido até 10Mb");
		}
		
		File saveFile = new File("C:\\temp\\"+fileName);
		try {
	        FileUtils.writeByteArrayToFile(saveFile, arquivo.getBytes());
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
		ra.addFlashAttribute("messageFileServer","Upload de arquivo realizado com sucesso!");
		
		return "redirect:/";
		
	}

}
