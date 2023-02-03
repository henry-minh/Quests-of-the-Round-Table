package org.vaadin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.QuestStageMediator;

@Configuration
public class QuestStageConfig {

    //This class doesn't do anything by itself,
    // it is referenced by the Spring context to create instances
    @Bean("stage0")
    public QuestStageMediator stage0(){
        return new QuestStageMediator(0);
    }

    @Bean("stage1")
    public QuestStageMediator stage1(){
        return new QuestStageMediator(1);
    }

    @Bean("stage2")
    public QuestStageMediator stage2(){
        return new QuestStageMediator(2);
    }

    @Bean("stage3")
    public QuestStageMediator stage3(){
        return new QuestStageMediator(3);
    }

    @Bean("stage4")
    public QuestStageMediator stage4(){
        return new QuestStageMediator(4);
    }
}
