package de.fh_dortmund.kekru001.projektarbeit.beispieldocker_ejb.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import lombok.val;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.beans.MongoLocal;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Anzeige;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.entities.Benutzer;
import de.fh_dortmund.kekru001.projektarbeit.beispieldocker_common.util.BeispielDockerUtils;

@Stateless
public class MongoBean implements MongoLocal, Serializable {


	private static final long serialVersionUID = 1L;

	public static final String COLLECTION_ENTITYIDS = "entityids";
	public static final String COLLECTION_BENUTZER = "benutzer";
	public static final String COLLECTION_ANZEIGE = "anzeige";
	private MongoDatabase db;
	private MongoClient mongoClient;

	@PostConstruct
	public void init(){
//				int port = 27017;
//				String host = "q.whiledo.de";
//				String host = "192.168.0.15";
		//		String host = "5.45.108.198";
		String host = BeispielDockerUtils.getPropertyOrSystemEnv("mongohost");
		int port = Integer.parseInt(BeispielDockerUtils.getPropertyOrSystemEnv("mongoport"));
		mongoClient = new MongoClient(host, port);

		db = mongoClient.getDatabase("myDB");
	}

	@PreDestroy
	public void cleanUp(){
		mongoClient.close();
	}

	private long nextId(){
		MongoCollection<Document> c = db.getCollection(COLLECTION_ENTITYIDS);
		val it = c.find().iterator();
		final String key = "previousid";
		while(it.hasNext()){
			val document = it.next();
			if(document.containsKey(key)){
				long lastId = document.getLong(key);
				long old = lastId;
				lastId++;
				c.replaceOne(new Document(key, old), new Document(key, lastId));
				System.out.println(lastId);
				return lastId;
			}
		}

		long id = 1;
		c.insertOne(new Document(key, id));
		System.out.println("its " + id);
		return id;
	}

	@Override
	public Anzeige save(Anzeige a) {
		boolean insert = false;
		if(a.getId() == 0){
			a.setId(nextId());
			insert = true;
		}

		save(a.getErsteller());

		Document document = new Document("id", a.getId())
		.append("titel", a.getTitel())
		.append("text", a.getText())
		.append("preis", a.getPreis())
		.append("gewerblich", a.isGewerblich())
		.append("erstellerId", a.getErsteller() != null ? a.getErsteller().getId() : null);

		MongoCollection<Document> c = db.getCollection(COLLECTION_ANZEIGE);

		if(insert){
			c.insertOne(document);
		}else{
			c.replaceOne(new Document("id", a.getId()), document);
		}

		return a;
	}

	@Override
	public Benutzer save(Benutzer b) {
		boolean insert = false;
		if(b.getId() == 0){
			b.setId(nextId());
			insert = true;
		}

		Document document = new Document("id", b.getId())
		.append("name", b.getName())
		.append("email", b.getEmail())
		.append("password", b.getPassword());

		MongoCollection<Document> c = db.getCollection(COLLECTION_BENUTZER);

		if(insert){
			c.insertOne(document);
		}else{
			c.replaceOne(new Document("id", b.getId()), document);
		}

		return b;
	}

	@Override
	public List<Anzeige> findAllAnzeige() {
		return readAnzeigen(db.getCollection(COLLECTION_ANZEIGE).find());
	}

	@Override
	public List<Benutzer> findAllBenutzer() {
		return readBenutzer(db.getCollection(COLLECTION_BENUTZER).find());
	}

	@Override
	public Anzeige findByIdAnzeige(long id) {
		List<Anzeige> result = readAnzeigen(db.getCollection(COLLECTION_ANZEIGE).find(new BasicDBObject("id", id)));
		if(result.isEmpty()){
			return null;
		}
		return result.get(0);
	}

	@Override
	public Benutzer findByIdBenutzer(long id) {
		List<Benutzer> result = readBenutzer(db.getCollection(COLLECTION_BENUTZER).find(new BasicDBObject("id", id)));
		if(result.isEmpty()){
			return null;
		}
		return result.get(0);
	}

	private List<Anzeige> readAnzeigen(FindIterable<Document> c){
		val result = new LinkedList<Anzeige>();
		c.forEach(new Block<Document>() {

			@Override
			public void apply(Document b) {

				Anzeige a = new Anzeige();
				a.setId(b.getLong("id"));
				a.setTitel(b.getString("titel"));
				a.setText(b.getString("text"));
				a.setPreis(b.getInteger("preis"));
				a.setGewerblich(b.getBoolean("gewerblich"));
				a.setErsteller(findByIdBenutzer(b.getLong("erstellerId")));
				result.add(a);
			}
		});
		return result;
	}

	private List<Benutzer> readBenutzer(FindIterable<Document> c){
		val result = new LinkedList<Benutzer>();
		c.forEach(new Block<Document>() {

			@Override
			public void apply(Document b) {
				Benutzer a = new Benutzer();
				a.setId(b.getLong("id"));
				a.setName(b.getString("name"));
				a.setEmail(b.getString("email"));
				a.setPassword(b.getString("password"));
				result.add(a);
			}
		});
		return result;
	}

}
