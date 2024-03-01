package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * some controller class
 */
@Controller
@RequestMapping("/")
public class SomeController {

    /**
     * @return no description was provided in the template.
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }

    /**
     * javadoc
     * @param name name
     * @param title title
     * @return hello {name} is shown
     */
    @GetMapping("/name /{name}")
    @ResponseBody
    public String name(@PathVariable("name") String name ,
                       @RequestParam("title") String title) {
        var sb = new StringBuilder("Hello ");
        if(title != null) {
            sb.append(title ). append(' ');
        }
        sb.append(name );
        sb.append('!');
        return sb.toString ();
    }


}