package com.user.user_service.service;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.user.user_service.Apphelper.ProjectUtils;
import com.user.user_service.entity.Contacts;
import com.user.user_service.entity.ServiceResponse;
import com.user.user_service.entity.User;
import com.user.user_service.repository.UserRepository;

/**
 * @author Aritra
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository repo;
	
	@Autowired
	RestTemplate rt;
	
	@Value("${mongo.db.uri}")
	String connectionString;
	
	@Value("${spring.data.mongodb.database}")
	String defaultDbName;
	
	@Value("${mongo.db.collection}")
	String CollectionName;
	
	@Autowired
	ProjectUtils pu;

	@Override
	public ServiceResponse getUser(long UserId) {
		
		ServiceResponse sr = new ServiceResponse();
		try {
		    List<User> usersList = new ArrayList<User>();
		    if (repo.count()==0) {
		    	sr.setResMessage("No user present in the contact list, First Create a User to proceed.");
				sr.setStatus(HttpStatus.NO_CONTENT);
				return sr;
		    }
		    //To fetch all User details when admin logs-in.
		    if (UserId==9999999){
		    	List<User> fetchedUsers = repo.findAll();
		    	//Using lambda Function
		    	fetchedUsers.forEach(x -> {
		    		//For fetching details from a response as a List Directly.
		    		//List<Contacts> contacts = rt.getForObject("http://localhost:9002/microservice2/contacts/getContactsService?userId=" + x.getUserId(), List.class);
		    		//x.setContacts(contacts);
		    		
		    		//For fetching details from a response as a ServiceResponse Body.
		    		ServiceResponse srt = rt.getForObject("http://CONTACT-SERVICE/microservice2/contacts/getMyContacts?userId=" + x.getUserId(), ServiceResponse.class);
		    		if(srt.getStatus()==HttpStatus.OK){
		    				List<Contacts> contacts = new ObjectMapper().convertValue(srt.getResObject(), new TypeReference<List<Contacts>>() {});
		    				x.setContacts(contacts);  
		    				x.setContactCount(contacts.size());
		    				
		    		}
		    		usersList.add(x);
		    	});
		    	
		    	
		    	
		    }//To fetch a single/particular user
		    else if(UserId!=0){
		    		User fetchedUsers = repo.findByUserId(UserId);
		    		ServiceResponse srt = rt.getForObject("http://CONTACT-SERVICE/microservice2/contacts/getMyContacts?userId=" + fetchedUsers.getUserId(), ServiceResponse.class);
		    		if(srt.getStatus()==HttpStatus.OK){
		    				List<Contacts> contacts = new ObjectMapper().convertValue(srt.getResObject(), new TypeReference<List<Contacts>>() {});
		    				fetchedUsers.setContacts(contacts);    
		    				fetchedUsers.setContactCount(contacts.size());
		    				
		    		}
		    		fetchedUsers.setPassword(pu.EncryptPasswd(fetchedUsers.getPassword()));
		    		usersList.add(fetchedUsers);
		   
      
		    } 
		    //To check if any details have been found or not.
		    if(usersList.isEmpty()){
			    sr.setResMessage("Sorry! Could Not get your User. Please check the filtering variables");
				sr.setResObject("Empty");
				sr.setStatus(HttpStatus.BAD_REQUEST);
			    return sr;
		    }
		    sr.setResMessage("Successfully Found Your User");
			sr.setResObject(usersList);
			sr.setStatus(HttpStatus.OK);
		    return sr;
		  } catch (Exception e) {
			  	e.printStackTrace();
		    	sr.setResMessage("Error : Could not find your User or Users. Java Error: "+e.getMessage());
				sr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				
		    return sr;
	}

}
	
	@Override
	public ServiceResponse newUser(User userModel){
		ServiceResponse sr = new ServiceResponse();
		if(userModel.getPhone()!=null && userModel.getName()!=null) {
		try {
			userModel.setName(pu.removeUnwantedSpaces(userModel.getName()));
			userModel.setPassword(pu.createPassword(userModel.getName()));
			if(!pu.checkMyName(userModel.getName())) {
				sr.setResMessage("Please Enter a Proper Name");
				sr.setStatus(HttpStatus.BAD_REQUEST);
				return sr;
			}if(!pu.checkMyNumber(userModel.getPhone())) {
				sr.setResMessage("Please Enter a Proper Phone Number");
				sr.setStatus(HttpStatus.BAD_REQUEST);
				return sr;
			}
			if(repo.count()==0){
				userModel.setUserId(1L);
				User newUser = repo.insert(userModel);
				sr.setResMessage("Successfully Added You in the User list. Please carefully notedown the password as it will be necessary in future.");
				//Printing the newly added entry as an response Object.
				sr.setResObject(newUser);
				sr.setStatus(HttpStatus.CREATED);
			}
			else {
			 //Using MongoDb Aggregation pipeline to fetch the last present user's userId from the collection
				try(MongoClient client = MongoClients.create()){
					List<Bson> pipeline = new ArrayList<Bson>();
					Document projectSpec = new Document("$project", new Document("_id", 1L));
					pipeline.add(projectSpec);
					Document sortingSpec = new Document("$sort", new Document("_id", -1L));
					pipeline.add(sortingSpec);
					List<Document> dataList = client.getDatabase(defaultDbName).getCollection(CollectionName).aggregate(pipeline).into(new ArrayList<Document>());
					
					//converting the fetched latest/last userId from the collection to Long
					Long prevId=Long.parseLong(dataList.get(0).toString().substring(dataList.get(0).toString().indexOf("=")+1,dataList.get(0).toString().length()-2));
					userModel.setUserId(prevId+1);
					User newUser = repo.insert(userModel);
					sr.setResMessage("Successfully Added You in the User list. Please carefully notedown the password as it will be necessary in future.");
					//Printing the newly added entry as an response Object.
					sr.setResObject(newUser);
					sr.setStatus(HttpStatus.CREATED);
				}catch(Exception e){
					e.printStackTrace();
					sr.setResMessage("Error :"+e.toString());
					sr.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
					
				}
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}
		}else {
			sr.setResMessage("Error : Required Fields cannot be empty");
			sr.setStatus(HttpStatus.BAD_REQUEST);		
		}
		return sr;
	}
	
	@Override
	public ServiceResponse deleteUser(long userId){
		ServiceResponse sr = new ServiceResponse();
		try{
		if(repo.existsById(userId)){
			rt.delete("http://CONTACT-SERVICE/microservice2/contacts/deleteContact?userId=" + userId);
			repo.deleteById(userId);
			sr.setResMessage("The requested user is successfully deleted");
			sr.setStatus(HttpStatus.OK);

			
		} else {
			sr.setResMessage("The requested user is undefined i.e. no user found with that user-details.Please enter a proper userId");
			sr.setStatus(HttpStatus.BAD_REQUEST);
		}
		} catch(Exception e){
			e.printStackTrace();
		}
		return sr;
	}
	
	@Override
	public ServiceResponse editUser(long userId, User userModel){
		ServiceResponse sr = new ServiceResponse();
		try {
			User user = repo.findByUserId(userId);
			 if(user!=null){
				 if(userModel.getName()!=null){
					 userModel.setName(pu.removeUnwantedSpaces(userModel.getName()));
						if(!pu.checkMyName(userModel.getName())) {
							sr.setResMessage("Please Enter a Proper Name");
							sr.setStatus(HttpStatus.BAD_REQUEST);
							return sr;
						}
				 }
				 if(userModel.getPhone()!=null){
					 user.setPhone(userModel.getPhone());
					 if(!pu.checkMyNumber(userModel.getPhone())) {
							sr.setResMessage("Please Enter a Proper Phone Number");
							sr.setStatus(HttpStatus.BAD_REQUEST);
							return sr;
						}
				 }
				User newUser= repo.save(user);
				 sr.setResObject(newUser);
				 sr.setResMessage("User successfully updated");
				 sr.setStatus(HttpStatus.OK);
			 }
			 else{
				 sr.setResMessage("Cannot Update the given user as there is no user with that user-details");
				 sr.setStatus(HttpStatus.BAD_REQUEST);
			 }
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return sr;
	}

}
