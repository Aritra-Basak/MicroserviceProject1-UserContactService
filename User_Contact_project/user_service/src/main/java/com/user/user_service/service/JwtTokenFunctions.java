package com.user.user_service.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.user.user_service.entity.AuthenticationRequest;
import com.user.user_service.entity.AuthenticationResponse;
import com.user.user_service.entity.ServiceResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenFunctions {
	@Value("${mongo.db.uri}")
	String connectionString;
	
	@Value("${spring.data.mongodb.database}")
	String defaultDbName;
	
	@Value("${mongo.db.collection}")
	String CollectionName;
	
	private static final String SECRETY_KEY = "sEcrEtkEy";
    private final long expirationTime = 1000 * 60 * 30; // Token expiration time (in milliseconds)
    public ServiceResponse getAuthToken(AuthenticationRequest auth, HttpServletRequest request) {
    	String xAccessToken = request.getHeader("X-ACCESS-TOKEN");
    	ServiceResponse response = new ServiceResponse();
    	if(xAccessToken.equals("give-me-jwt")){
	    		if((auth.getUserName()==""||auth.getUserName().isEmpty())&&(auth.getPassword()==""||auth.getPassword().isEmpty())){
	    			response.setResMessage("Input Fields cannot be empty");
	    			response.setStatus(HttpStatus.BAD_REQUEST);
	    		}
	    		String jwtToken = generateToken(auth.getUserName(),auth.getPassword());
	    		Date expiry = getTokenExpirationDate(jwtToken);
	    		AuthenticationResponse authResponse = new AuthenticationResponse();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				authResponse.setToken(jwtToken);
				authResponse.setExpiredOn(sdf.format(expiry));
				authResponse.setTokenType("Bearer");
				response.setResMessage("Hello! "+auth.getUserName()+" Your JWT Token is generated successfully");
				response.setStatus(HttpStatus.OK);
				response.setResObject(authResponse);
    		}
    	if(!xAccessToken.equals("give-me-jwt")){
    			response.setResMessage("Your API key is not matching");
				response.setStatus(HttpStatus.BAD_REQUEST);
	    	}
    	if(xAccessToken.isEmpty()){
	    		response.setResMessage("Your API key is missing");
				response.setStatus(HttpStatus.BAD_REQUEST);
	    	}
	    return response;
    } 		
    
    public String generateToken(String username, String password) {
        return Jwts.builder()
                .setSubject(username)
                .claim("password", password)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRETY_KEY)
                .compact();
    }
 
    public Date getTokenExpirationDate(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRETY_KEY).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }
    
    public long verifyUser(String authorizationHeader) {
        String token = null;
        long userId=0;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Excluding "Bearer " prefix
        }
        Claims claims = Jwts.parser().setSigningKey(SECRETY_KEY).parseClaimsJws(token).getBody();
        String userName =claims.getSubject();
        String userPassword =(String)claims.get("password");
        if(userPassword.equals("admin0987654321") && userName.equals("Admin")) {
        	userId=9999999;
        }else {
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
				List<Document> dataList = client.getDatabase(defaultDbName).getCollection(CollectionName).aggregate(pipeline).into(new ArrayList<Document>());
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
        }
        return userId;
    }

}
