/**
 * 
 */
package com.contacts.UserContacts.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.contacts.UserContacts.entity.Contacts;
import com.contacts.UserContacts.entity.ServiceResponse;
import com.contacts.UserContacts.repository.ContactRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * @author Aritra
 *
 */
@Service
public class ContactServiceImpl implements ContactService {

	@Autowired
	ContactRepository cr;

	@Value("${mongo.db.uri}")
	String connectionString;
	
	@Value("${spring.data.mongodb.database}")
	String defaultDbName;
	
	@Value("${mongo.db.collection}")
	String CollectionName;
	
	@Override
	public ServiceResponse getContactDetails(long userId) {
		ServiceResponse sr = new ServiceResponse();
		List<Contacts> cont = new ArrayList<>();
		if(userId==0)
		{
			System.out.println("User Id cannot be null or 0");
			sr.setResMessage("User Id cannot be null or 0");
			sr.setStatus(HttpStatus.BAD_REQUEST);
			
		}
		else
		{
			cr.findByUserId(userId).forEach(cont::add);
			if(cont.isEmpty())
			{
				System.out.println("Contact Details Not found");
				sr.setResMessage("Contact Details Not found");
				sr.setStatus(HttpStatus.BAD_REQUEST);
			}
			else {
				System.out.println("Your Contact list found successfully");
				System.out.println(cont);
				sr.setResMessage("Your Contact list found successfully");
				sr.setResObject(cont);
				sr.setStatus(HttpStatus.OK);
			}

		}
		return sr;
	}
	
	@Override
	public List<Contacts> contactForUser(long userId)
	{
		List<Contacts> cont = new ArrayList<>();
		if(userId==0)
		{
			cont=null;
		}
		else
		{
			cr.findByUserId(userId).forEach(cont::add);
			if(cont.isEmpty())
			{
				System.out.println("Contact Details Not found");
				cont=null;
			}
			else
				System.out.println("Your Contact list found successfully");
		}
		return cont;
	}

	@Override
	public ServiceResponse createNewContact(Contacts contactModel,long userId)
	{
		ServiceResponse sr = new ServiceResponse();
		try {
			contactModel.setName(Utils.removeUnwantedSpaces(contactModel.getName()));
			if(!Utils.checkMyName(contactModel.getName())){
				sr.setResMessage("Please Enter a Proper Name");
				sr.setStatus(HttpStatus.BAD_REQUEST);
				return sr;
			}if((!Utils.checkMyNumber(contactModel.getContactNumber()))) {
				sr.setResMessage("Please Enter a Proper Phone Number");
				sr.setStatus(HttpStatus.BAD_REQUEST);
				return sr;
			}
			if(cr.count()==0)
			{
				contactModel.setcId(1L);
				contactModel.setUserId(userId);
				Contacts newUser = cr.insert(contactModel);
				sr.setResMessage("Successfully Added Your Contact w.r.t your given user's userId.");
				//Printing the newly added entry as an response Object.
				sr.setResObject(newUser);
				sr.setStatus(HttpStatus.CREATED);
			} else{
			 //Using MongoDb Aggregation pipeline to fetch the last present document's cId from the collection
				try(MongoClient client = MongoClients.create()){
				List<Bson> pipeline = new ArrayList<Bson>();
				Document projectSpec = new Document("$project", new Document("_id", 1L));
				pipeline.add(projectSpec);
				Document sortingSpec = new Document("$sort", new Document("_id", -1L));
				pipeline.add(sortingSpec);
				List<Document> dataList = client.getDatabase(defaultDbName).getCollection(CollectionName).aggregate(pipeline).into(new ArrayList<Document>());
				client.close();
				//converting the fetched latest/last cId from the collection to Long
				Long prevId=Long.parseLong(dataList.get(0).toString().substring(dataList.get(0).toString().indexOf("=")+1,dataList.get(0).toString().length()-2));
				contactModel.setcId(prevId+1);
				contactModel.setUserId(userId);
				Contacts newUser = cr.insert(contactModel);
				sr.setResMessage("Successfully Added Your Contact w.r.t your given user's userId.");
				//Printing the newly added entry as an response Object.
				sr.setResObject(newUser);
				sr.setStatus(HttpStatus.CREATED);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return sr;
	}

	@Override
	public ServiceResponse deleteContactByUserId(long userId)
	{
		ServiceResponse sr = new ServiceResponse();
		cr.deleteByUserId(userId);
		sr.setResMessage("The Contact Details with the given user Id is successfully deleted");
		sr.setStatus(HttpStatus.OK);
		return sr;
	}
	@Override
	public ServiceResponse deletContactByCiD(long cId)
	{
		ServiceResponse sr = new ServiceResponse();
		try {
			if(cr.existsById(cId))
			{
			cr.deleteById(cId);
			sr.setResMessage("The Contact Details with the given Comtact Id is successfully deleted");
			sr.setStatus(HttpStatus.OK);
			}
			else
			{
				sr.setResMessage("The given Contact Id is undefined i.e. no contact details found with that Contact Id. Please enter a proper contact Id");
				sr.setStatus(HttpStatus.BAD_REQUEST);
			}

		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return sr;
	}
	
	@Override
	public ServiceResponse editContactByCiD(Contacts contactModel,long contactId){
		ServiceResponse sr = new ServiceResponse();
		try {
			Contacts cont = cr.findBycId(contactId);
			 if(cont!=null){
				 if(contactModel.getName()!=null){
					 contactModel.setName(Utils.removeUnwantedSpaces(contactModel.getName()));
						if(!Utils.checkMyName(contactModel.getName())){
							sr.setResMessage("Please Enter a Proper Name");
							sr.setStatus(HttpStatus.BAD_REQUEST);
							return sr;
						}
					 cont.setName(contactModel.getName());
				 }
				 if(contactModel.getContactNumber()!=null){
					 if((!Utils.checkMyNumber(contactModel.getContactNumber()))) {
							sr.setResMessage("Please Enter a Proper Phone Number");
							sr.setStatus(HttpStatus.BAD_REQUEST);
							return sr;
						}
					 cont.setContactNumber(contactModel.getContactNumber());
				 }
				Contacts newUser= cr.save(cont);
				 sr.setResObject(newUser);
				 sr.setResMessage("User successfully updated");
				 sr.setStatus(HttpStatus.OK);
			 }
			 else{
				 sr.setResMessage("Cannot Update the given contact as there is no contact with that contact-details");
				 sr.setStatus(HttpStatus.BAD_REQUEST);
			 }
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return sr;
	}

}
class Utils
{
	public static boolean checkMyName(String inputStr) {
		boolean res = false;
		 Pattern pattern = Pattern.compile(new String ("^[a-zA-Z\\s]*$"));
		    Matcher matcher = pattern.matcher(inputStr);
		    if(matcher.matches()){
		        res =true;
		    }
		    return res;
		 
	}
	
	public static boolean checkMyNumber(String inputStr) {
		if(inputStr.length()==10) {
			Pattern pattern = Pattern.compile(new String ("^[6-9]\\d{9}$"));//Meaning-> /^[6-9]-> means the starting digit must be 6,7,8,9 and the rest 9 digits can be any number
		    Matcher matcher = pattern.matcher(inputStr);
		    if(matcher.matches()){
		        return true;
		    }
		    return false;
		}else {
			return false;
		}
	}
	public static String removeUnwantedSpaces(String passedName){
		String name="";
		passedName.trim();
		name =passedName.replaceAll("\\s+"," ");
		//System.out.println(name);
		return name;	
	}
}
