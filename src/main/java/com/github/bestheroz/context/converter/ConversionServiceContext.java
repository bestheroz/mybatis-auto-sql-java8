package com.github.bestheroz.context.converter;

import com.google.common.collect.ImmutableSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ConversionServiceContext extends DefaultConversionService
    implements ConversionService {

  @Bean(name = "conversionService")
  public ConversionService getConversionService() {
    final ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
    bean.setConverters(ImmutableSet.of(new InstantConverter()));
    bean.afterPropertiesSet();
    return bean.getObject();
  }
}
