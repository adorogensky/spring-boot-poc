package training.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Application.class)
public class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootEndpointReturnsHello() throws Exception {
        mockMvc.perform(
            get("/")
        ).andExpect(
            status().isOk()
        ).andExpect(
            content().string("Hello World!")
        );
    }

    @Test
    void rootEndpointWithWhoEqualsAlexReturnsHelloAlex() throws Exception {
        mockMvc.perform(
            get("/").queryParam("who", "Alex")
        ).andExpect(
            status().isOk()
        ).andExpect(
            content().string("Hello Alex!")
        );
    }
}
