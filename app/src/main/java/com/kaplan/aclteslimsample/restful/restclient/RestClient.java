package com.kaplan.aclteslimsample.restful.restclient;


import com.kaplan.aclteslimsample.restful.model.Response;
import com.kaplan.aclteslimsample.util.NetworkConstants;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Rest(rootUrl = NetworkConstants.BASE_URL, converters = {GsonHttpMessageConverter.class, StringHttpMessageConverter.class})
public interface RestClient extends RestClientHeaders {

	@Post(NetworkConstants.FETCH_IMAGE)
	Response getPhotos(String api_key, String format,String nojsoncallback);


}
