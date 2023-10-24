package ua.zxc.cowbot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ua.zxc.cowbot.utils.BinaryTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@TestConfiguration
public class AppConfigTest {

    @Bean(name = "respects")
    public BinaryTree respectsList() {
        BinaryTree tree = new BinaryTree("дякую", "дякую");
        Resource resource = new ClassPathResource("/respects.txt");
        return getBinaryTree(tree, resource);
    }

    @Bean(name = "disrespects")
    public BinaryTree disrespectsList() {
        BinaryTree tree = new BinaryTree("бан", "бан");
        Resource resource = new ClassPathResource("/disrespects.txt");
        return getBinaryTree(tree, resource);
    }

    private BinaryTree getBinaryTree(BinaryTree tree, Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            try (Scanner s = new Scanner(inputStream)) {
                while (s.hasNext()) {
                    String line = s.nextLine();
                    tree.put(line, line);
                }
                return tree;
            }
        } catch (IOException e) {
            return tree;
        }
    }

}