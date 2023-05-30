package com.yue.commit.config;

import com.yue.commit.service.AvoidRepeatableCommitAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = AvoidRepeatableCommitAspect.class)
public class RepeatableCommitAutoConfigure {
}
