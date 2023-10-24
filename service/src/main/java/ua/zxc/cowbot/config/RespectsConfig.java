package ua.zxc.cowbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ua.zxc.cowbot.utils.BinaryTree;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Slf4j
@Configuration
public class RespectsConfig {

    @Bean(name = "respectText")
    public BinaryTree respectText() {
        BinaryTree tree = new BinaryTree("", "respect");
        Resource resource = new ClassPathResource("/respects.txt");
        getBinaryTree(tree, resource, "respect");

        resource = new ClassPathResource("/disrespects.txt");
        getBinaryTree(tree, resource, "disrespect");

        return tree;
    }

    @Bean(name = "respectSticker")
    public BinaryTree respectSticker() {
        BinaryTree tree = new BinaryTree("", "respect");
        Resource resource = new ClassPathResource("/respects_emoji.txt");
        getBinaryTree(tree, resource, "respect");

        resource = new ClassPathResource("/disrespects_emoji.txt");
        getBinaryTree(tree, resource, "disrespect");

        return tree;
    }

    private void getBinaryTree(BinaryTree tree, Resource resource, String value) {
        try {
            InputStream inputStream = resource.getInputStream();
            try (Scanner s = new Scanner(inputStream)) {
                while (s.hasNext()) {
                    String key = s.nextLine();
                    tree.put(key, value);
                }
            }
        } catch (IOException e) {
            log.warn("Can not to init respects list: resource={}, value={}", resource, value, e);
        }
    }
}
