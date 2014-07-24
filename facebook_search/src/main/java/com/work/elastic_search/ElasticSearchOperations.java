package com.work.elastic_search;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.restfb.json.JsonObject;

public class ElasticSearchOperations {

	String ip,index,type;
	int port;
	Client client;
	
	public ElasticSearchOperations(String ip, int port){
		this.ip=ip;
		this.port=port;
	}
	
	public Client getClient(){
		client= new TransportClient().addTransportAddress(new InetSocketTransportAddress(ip, port));
		return client;
	}
	

	public void createIndex(String index, String type) {
		this.index=index;
		this.type=type;
		if(isIndexExist() == false){
			client.admin().indices().prepareCreate(index).execute().actionGet();
		}
}
	public boolean isIndexExist() {
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
	public void index(JsonObject data){
		client.prepareIndex(index,type)
		.setSource(data.toString())
		.setRefresh(true)
		.execute()
		.actionGet();
	}
	
	public static void main(String[] args) {
		
	}

}
