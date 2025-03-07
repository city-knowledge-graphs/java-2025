package lab6;

import java.io.FileNotFoundException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import util.ReadFile;


public class QueryLocalRDFGraph_Playground {
	
	public QueryLocalRDFGraph_Playground(String file) throws FileNotFoundException {
	
		Dataset dataset = RDFDataMgr.loadDataset(file, RDFLanguages.TURTLE);
		Model model = dataset.getDefaultModel();
		
		
		 //Query local model
	    String queryStr =
	     "PREFIX ttr: <http://example.org/tuto/resource#>" +
	     "PREFIX tto: <http://example.org/tuto/ontology#>" +
	     "PREFIX dbp: <http://dbpedia.org/property/>" +
	     "SELECT ?thing ?name where {" +
	      "?thing tto:sex \"female\" ." + 
	      "?thing dbp:name ?name ." +
	    "}";
	    
	    
	    
	    Query q = QueryFactory.create(queryStr);
		
	    System.out.println("Querying local model: ");
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
		try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					RDFNode thing = soln.get("?thing");
					RDFNode name = soln.get("?name");					
					System.out.println(thing + " is female with name '" + name + "'.");					
		}
			    
		} finally {
			qe.close();
		}
				
		//The same but load query from file
		String query_file = "files/lab6/query.txt";
		ReadFile qfile = new ReadFile(query_file);		
		queryStr = qfile.readFileIntoString();
				
		q = QueryFactory.create(queryStr);
				
		System.out.println("Querying local model (query from file): ");
				
		qe = QueryExecutionFactory.create(q, model);
		try {
			ResultSet res = qe.execSelect();
			while( res.hasNext()) {
				QuerySolution soln = res.next();
				RDFNode thing = soln.get("?thing");
				RDFNode name = soln.get("?name");					
				System.out.println(thing + " is female with name '" + name + "'.");					
			}			    
		} finally {
			qe.close();
		}
		
	    
	}
	
	
	
			
	
	public static void main(String[] args) {

		try {
			new QueryLocalRDFGraph_Playground("files/lab6/playground.ttl");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
