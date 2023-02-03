package org.vaadin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.Rank;

@Configuration
public class RankConfig {

    @Bean("Champion")
    public Rank champion(){
        return new Rank("Champion Knight", 10, 20);
    }

    @Bean("Knight")
    public Rank knight(){
        return new Rank("Knight", 7, 10);
    }

    @Bean("Squire")
    public Rank squire(){
        return new Rank("Squire", 5, 5);
    }
}
