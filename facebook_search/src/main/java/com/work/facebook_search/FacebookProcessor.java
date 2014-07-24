package com.work.facebook_search;

import java.util.HashSet;

import java.util.Set;


import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.json.JsonObject;
import com.restfb.types.Post;
import com.work.elastic_search.ElasticSearchOperations;

public class FacebookProcessor {

	FacebookClient facebookClient;
	Set<String> messages= new HashSet<String>();
	int searchDepth=3; 

	ElasticSearchOperations es= null;
	
	public void createFacebookConnections(){
		//https://developers.facebook.com/tools/explorer?method=GET&path=me%3Ffields%3Did%2Cname&version=v2.0
		facebookClient = new DefaultFacebookClient("CAACEdEose0cBALPxyIIzxPQUeQ1MrHQ8KgXE9dEbjky7ZCpI1B0DrrXxIFfRPRW3UPY6Pl7Cz3wsnWg3qvlikOsrs7BEWM0gpYobH0SpRgkMEjqfZCFJHE2wPgOe4eSbISonZAQOwWAZC8CabK9mNvd1AlzEuztp7CAqxZAZCLy3jQARKim4g11QNgpHpZCBtxZAefdIWzxcL0DAUZA42kIijrHzgRJprTxMZD");
		System.out.println(facebookClient);
	}
	public void createEsConnections(){
		 es=new ElasticSearchOperations("localhost", 9300) ;
		 es.getClient();
		 es.createIndex("facebook", "message");
	 }
	public void search(String keyword){
		Connection<Post> publicSearch =
				  facebookClient.fetchConnection("search", Post.class,
				    Parameter.with("q", keyword), Parameter.with("type", "post"));


		for(int i=0; i< publicSearch.getData().size();i++){
			String message=publicSearch.getData().get(i).getMessage();
			if(message!=null)
				messages.add(message);
		}
	}
	 
	public void execute(String keyword){
		createFacebookConnections();
		while(searchDepth!=0){
			search(keyword);	
			searchDepth--;
		}
		System.out.println(messages);
		System.out.println("length "+messages.size());
		
		createEsConnections();
		
		JsonObject data = new JsonObject();
		data.put("keyword", keyword);
		data.put("messages", messages);
		es.index(data);
	}
	
	public static void main(String[] args) {
		FacebookProcessor fb = new FacebookProcessor();
		fb.execute("sachin");
	}

	
}
