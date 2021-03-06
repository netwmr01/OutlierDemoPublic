package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View controller to handle request mapping for view change
 * @author Hui Zheng
 *
 */
@Controller
public class ViewController {

	/**
	 * Return the initial to the client
	 * @param name not required, test for parameter change
	 * @return
	 */
	@RequestMapping("/")
	public String indexView(@RequestParam(value="name",required=false, defaultValue="World") String name) {
		return "index";
	}
	
	@RequestMapping("/uploadfiles")
	public String uploadView(){
		return "uploadfiles";
	}
	
	/**
	 * refreash the whole index page and load the new dataplane
	 * @param filename
	 * @return
	 */
	@RequestMapping("/custom")
	public String customView(@RequestParam(value="filename") String filename){
		MethodController.changeFileName(filename);
		return "index";
	}
	/**
	 * give the aboutus page
	 * @return
	 */
	@RequestMapping("/aboutus")
	public String aboutus(){
		return "aboutus";
	}
}
