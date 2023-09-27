package com.contacts.UserContacts.Service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtTokenFunctionsContacts {

	@Value("${mongo.db.uri}")
	String connectionString;
	
	@Value("${spring.data.mongodb.database}")
	String defaultDbName;
	

	private static final String SECRETY_KEY = "sEcrEtkEy";
	
	 public long verifyUser(String authorizationHeader) {
	        String token = null;
	        long userId=0;
	        
	        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            token = authorizationHeader.substring(7); // Excluding "Bearer " prefix
	        }
	        Claims claims = Jwts.parser().setSigningKey(SECRETY_KEY).parseClaimsJws(token).getBody();
	        String userName =claims.getSubject();
	        String userPassword =(String)claims.get("password");
	        	try(MongoClient client = MongoClients.create()){
					List<Bson> pipeline = new ArrayList<Bson>();
					Document projectSpec = new Document("$project", new Document("name", 1L)
				            .append("password", 1L)
				            .append("_id", 1L));
					pipeline.add(projectSpec);
					Document matchingSpec =new Document("$match",new Document("name", userName));
					pipeline.add(matchingSpec);
					Document matchingSpec2 =new Document("$match", new Document("password", userPassword));
					pipeline.add(matchingSpec2);
					List<Document> dataList = client.getDatabase(defaultDbName).getCollection("Users").aggregate(pipeline).into(new ArrayList<Document>());
					if(dataList.isEmpty())
						return userId;
					for(Document x:dataList) {
						 Object idValue = x.get("_id");
						 if(idValue!=null) {
							 userId =(long)idValue;
						 }
					}
	        	}catch(Exception e){
					e.printStackTrace();
				}
	        return userId;
	    }

	
}
