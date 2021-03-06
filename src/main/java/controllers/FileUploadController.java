package controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * This is an controller to get file from front-end
 * Contains all methods handle file upload, file scan
 * @author Hui Zheng
 *
 */
@Controller
public class FileUploadController {
	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);
	static String rootPath = "data";
	
	public FileUploadController() {
		// TODO Auto-generated constructor stub
		Resource resource =new ClassPathResource(rootPath);
		
		try {
			rootPath=resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("File upload constructor error"+e.getMessage());
		}
	}

	/** 
	 * handle file upload and save it into 
	 * 		Resource resource =new ClassPathResource(rootPath);
		
		try {
			rootPath=resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 * each data file will have it's own folder
	 * @param file, the file that user upload
	 * @return message weather saved successful or not
	 */
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file){
		logger.info("Already into the handelFileupload controller!");
		System.out.println("Already into the handelFileupload controller!");
		String name = null;
		if (!file.isEmpty()) {
			try {

				name = file.getOriginalFilename();
				String foldername = FilenameUtils.removeExtension(name);
				byte[] bytes = file.getBytes();

				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				File serverFilePath = new File(dir.getAbsolutePath()
						+ File.separator + foldername);
				if (!serverFilePath.exists())
					serverFilePath.mkdirs();
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + foldername+File.separator+ name);

				BufferedOutputStream stream =
						new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				logger.info("Server File Location="
						+ serverFile.getAbsolutePath());
				MethodController.preComputeAllFilesImpl();
				return "You successfully uploaded " + name + "! "+"Precomputation finished! ";
			} catch (Exception e) {
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name + " because the file was empty.";
		}
	}


	/**
	 * check existing file list
	 * @return
	 */
	@RequestMapping(value="/getExistingFileList")
	public @ResponseBody LinkedHashSet<String> getExistingFileList(){
		return getFileList();
	}

	/** the Implementation of getFileList
	 * @return
	 */
	public LinkedHashSet<String> getFileList(){
		LinkedHashSet<String> fileSet = new LinkedHashSet<String>();
		File directory = new File(rootPath);
		File[] fList = directory.listFiles();
		for(final File f: fList){
			if(f.isDirectory()){
				FileFilter filter =new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith(f.getName());							
					}
				};
				File[] result = f.listFiles(filter);
				if(result.length!=0){
					fileSet.add(result[0].getName());
				}
			}
		}
		return fileSet;
	}

	@RequestMapping(value="/getComputedFileList")
	public @ResponseBody LinkedHashSet<String> getComputedFileList(){
		return getComputedFileListImpl(true);
	}

	@RequestMapping(value="/getNotComputedFileList")
	public @ResponseBody LinkedHashSet<String> getNotComputedFileList(){
		return getComputedFileListImpl(false);
	}

	/**
	 * @param isComputed true for computed false for not
	 * @return the set of file name which contains edges.json, graph.json, group0-3.json, node.json
	 */
	static public LinkedHashSet<String> getComputedFileListImpl(boolean isComputed){
		LinkedHashSet<String> fileSet = new LinkedHashSet<String>();
		File directory = new File(rootPath);
		File[] fList = directory.listFiles();
		for(final File f: fList){
			if(f.isDirectory()){
				FileFilter filter =new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith("group");							
					}
				};
				FileFilter dataSetNameFilter = new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith(f.getName());							
					}
				};
				FileFilter graphFilter = new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith("graph");							
					}
				};
				FileFilter edgesFilter = new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith("edges");							
					}
				};
				FileFilter nodesFilter = new FileFilter(){
					public boolean accept(File file){
						return file.getName().startsWith("nodes");							
					}
				};
				File[] groupResult = f.listFiles(filter);
				File[] dataSetResult=f.listFiles(dataSetNameFilter);
				File[] graphResult=f.listFiles(graphFilter);
				File[] edgesResult=f.listFiles(edgesFilter);
				File[] nodesResult=f.listFiles(nodesFilter);
				boolean isQualified;
				isQualified = isComputed? 
						(groupResult.length==4
						&&graphResult.length==1
						&&edgesResult.length==1
						&&nodesResult.length==1
						&&dataSetResult.length!=0):
							((groupResult.length!=4
							||graphResult.length!=1
							||edgesResult.length!=1
							||nodesResult.length!=1)
									&&dataSetResult.length!=0);
						if(isQualified){
							fileSet.add(dataSetResult[0].getName());
						}else{
							System.out.println("!!!Data file filtered get #: "
									+dataSetResult.length
									+"\n"
									+"group#.json files #: "
									+groupResult.length);
						}
			}
		}

		return fileSet;
	}



}
