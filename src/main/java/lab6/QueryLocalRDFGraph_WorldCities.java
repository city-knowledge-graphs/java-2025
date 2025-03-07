package lab6;

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

public class QueryLocalRDFGraph_WorldCities {
	
	
	public QueryLocalRDFGraph_WorldCities(String file) {
		
		Dataset dataset = RDFDataMgr.loadDataset(file, RDFLanguages.TURTLE);
		Model model = dataset.getDefaultModel();
		
		
		//Query local model
	    //Return Cities
	    String queryStr =	            
	           	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
	        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
	        	"PREFIX wco:   <http://www.semanticweb.org/ernesto/in3067-inm713/wco/> \n" +
	           	"SELECT DISTINCT ?city WHERE { \n" +
	            "?city rdf:type wco:City .  \n" +
	            "\n}";
	             
	    
	    
	    Query q = QueryFactory.create(queryStr);
		
	    System.out.println("Querying local model, distinct cities: ");
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();					
					RDFNode city = soln.get("?city"); //Variable city in select	
					System.out.println("\t'"+ city + "'.");					
				}
			    
				} finally {
				qe.close();
				}
	    }
			
	
	public static void main(String[] args) {

		new QueryLocalRDFGraph_WorldCities("files/lab6/worldcities-free-100-task1.ttl");
		
	}
	

}
