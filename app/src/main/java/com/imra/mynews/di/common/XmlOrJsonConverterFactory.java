package com.imra.mynews.di.common;

import com.google.gson.GsonBuilder;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Date: 23.05.2020
 * Time: 20:46
 *
 * @author IMRA027
 */
public class XmlOrJsonConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for(Annotation annotation : annotations) {
            if(annotation.annotationType() == Xml.class) {
                return SimpleXmlConverterFactory.createNonStrict(
                        new Persister(new AnnotationStrategy())).responseBodyConverter(type, annotations, retrofit);
            }
            if(annotation.annotationType() == Scalar.class) {
                return ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit);
            }
        }
        return null;
    }
}
