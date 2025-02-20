package lab5;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
import org.apache.jena.riot.RDFFormat;


/**
 * Adapted 2024
 * 
 * @author ernesto
 *
 */
public class RDFSReasoning {

	
	public RDFSReasoning(String file_input, String file_ouput) throws FileNotFoundException {
	
		Dataset dataset = RDFDataMgr.loadDataset(file_input);
		Model model = dataset.getDefaultModel();
		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");
		
		
		//Option 1
		//Get defaults reasoners
		//Reasoner reasoner = ReasonerRegistry.getRDFSSimpleReasoner();
		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();		//Includes also axiomatic triples
		InfModel inf_model = ModelFactory.createInfModel(reasoner, model);
		
		//Option 2
		//Uses a RDFS reasoner internally
		//InfModel inf_model = ModelFactory.createRDFSModel(model);
		
		System.out.println("The graph witn RDFS inferences contains '" + inf_model.listStatements().toSet().size() + "' triples.");
		
		
		
		System.out.println("\nChecking entailments: ");
		String triple1 = ":Father rdfs:subClassOf :Person .";
		checkEntailment(inf_model, triple1);
		
		
		
		
		//Storing in RDF/xml
        OutputStream out = new FileOutputStream(file_ouput);
        RDFDataMgr.write(out, inf_model, RDFFormat.TURTLE);
	}
	
	
	
	public void checkEntailment(InfModel inf_model, String triple) {
		
		 //Query local model
	    String queryStr =
	     "PREFIX : <http://city.ac.uk/kg/lab4/>" +
	     "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
	     "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
	     "ASK {" +
	      triple + 
	    "}";
	    
	    Query q = QueryFactory.create(queryStr);
		
	   
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, inf_model);
				try {	
					
					//Different methods for SELECT (execSelect()) and ASK (execAsk()) queries:
					System.out.println("Does '" + triple + "' holds? " + qe.execAsk());
				
				} finally {
				qe.close();
				}
		
	}

	
	
	
	
	
	
	
	public static void main(String[] args) {

		try {
			
			new RDFSReasoning("files/lab5/lab5-rdfs.ttl", "files/lab5/lab5-rdfs-extended.ttl");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
