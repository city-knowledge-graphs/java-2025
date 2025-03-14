package lab6;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import util.WriteFile;

public class QueryLocalRDFGraph_WorldCities {
	
	
	InfModel inf_model; //Model after reasoning
	Model model; ///Model pre-reasoning
	
	public QueryLocalRDFGraph_WorldCities(String file) {
		
		Dataset dataset = RDFDataMgr.loadDataset(file, RDFLanguages.TURTLE);
		model = dataset.getDefaultModel();
		
		System.out.println("Loaded triples: '" + model.listStatements().toSet().size() + "'.");
		
		
		
	}
	
	
	public void perfromBasicQuery() {
		
		//Query local model
	    //Return Cities
	    String queryStr =	            
	           	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
	        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
	        	"PREFIX wco:   <http://www.semanticweb.org/ernesto/in3067-inm713/wco/> \n" +
	           	"SELECT DISTINCT ?city WHERE { \n" +
	            "?city rdf:type wco:City .  \n" +
	            "\n}"+
	            "LIMIT 10";
	             
	    
	    
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
	
	
	
    public void performReasoning(String ontology_file) {
        
        //We expand the graph with the inferred triples
        //We use approximate OWL 2 reasoner close to OWL 2 RL (but not exactly)
        
    	System.out.println("\nPerforming reasoning: ");
    	
		        
        //We should load the ontology first        
        Dataset dataset = RDFDataMgr.loadDataset(ontology_file);
        model.add(dataset.getDefaultModel().listStatements().toList());
        
        
        System.out.println("Triples including ontology: '" + model.listStatements().toSet().size() + "'.");
        
        Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();		
		inf_model = ModelFactory.createInfModel(reasoner, model);
		
        
        System.out.println("Triples after reasoning: '" + inf_model.listStatements().toSet().size() + "'.");
        
        
    }
    
    
    public void performSPARQLQuery(String file_query_out) {
    	
    	WriteFile writer = new WriteFile(file_query_out);
    	
    	System.out.println("\nCities with population > 5000000:");
    	
        
    	String queryStr = 
	            "PREFIX wco: <http://www.semanticweb.org/ernesto/in3067-inm713/wco/>" +
	           	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
	        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
	           	"SELECT DISTINCT ?country ?city ?pop WHERE {" +
	            "?city rdf:type wco:City ." +
	            "?city wco:isCapitalOf ?country ." +
	            "?city wco:population ?pop ." +
	            //"FILTER (?pop > xsd:integer(\"5000000\"))" +
	            "FILTER (xsd:integer(?pop) > 5000000)" +
	        "}"+
	        "ORDER BY DESC(?pop)";
	
    	Query q = QueryFactory.create(queryStr);
			
		QueryExecution qe =
					QueryExecutionFactory.create(q, inf_model);  //query over inferred model
		try {
				ResultSet res = qe.execSelect();
					
				int solutions = 0;
					
				while( res.hasNext()) {
					solutions++;
					QuerySolution soln = res.next();
					RDFNode country = soln.get("?country");
					RDFNode city = soln.get("?city");
					RDFNode population = soln.get("?pop");
					
					writer.writeLine(country.toString()+","+city.toString()+","+population.toString()+",");
					
				}
				System.out.println(solutions + " capitals satisfying the query.");
				    
		} finally {
				qe.close();
		}
			
		
		System.out.println("Saving query results in '" + file_query_out + "'.");
		writer.closeBuffer();
    
				
    }
	
	
			
	
	public static void main(String[] args) {

		QueryLocalRDFGraph_WorldCities queryWCD = new QueryLocalRDFGraph_WorldCities("files/lab6/worldcities-free-100-task1.ttl");
		
		queryWCD.perfromBasicQuery();
		
		
		//Solution reasoning
		queryWCD.performReasoning("files/lab6/ontology_worldcities.ttl");
		
		//Solution query
		queryWCD.performSPARQLQuery("files/lab6/query_result_world_cities.csv");
		
		
	}
	

}
