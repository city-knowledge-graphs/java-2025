package lab7;

import java.io.FileNotFoundException;
import java.util.Iterator;

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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import util.ReadFile;

/**
 * Author: Ernesto Jimenez-Ruiz
 * Modified in 2025
 */
public class QueryLocalRDFKnowledgeGraphs {
	
	protected enum JenaReasoner {MICRO, MINI}

	
	public QueryLocalRDFKnowledgeGraphs(String file_onto, String file_data, String query_file) throws FileNotFoundException {
		//Lang format, Not required
		
		//Load data
		Dataset dataset = RDFDataMgr.loadDataset(file_data);//, format);
		Model model = dataset.getDefaultModel();
		
		//Load ontology if any....
		if (file_onto!=null) {
	        Dataset dataset_onto = RDFDataMgr.loadDataset(file_onto);
	        model.add(dataset_onto.getDefaultModel().listStatements().toList());
		}		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");

		
	    //Do reasoning
	    Reasoner reasoner;
	    JenaReasoner jenaReasoner=JenaReasoner.MICRO;
	    
		if (jenaReasoner==JenaReasoner.MINI)
			reasoner = ReasonerRegistry.getOWLMiniReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly)
		else
			reasoner = ReasonerRegistry.getOWLMicroReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly). Less expressive but faster than Mini reasoner.
				
		InfModel inf_model = ModelFactory.createInfModel(reasoner, model);
	    
		System.out.println("The graph witn inferences contains '" + inf_model.listStatements().toSet().size() + "' triples.");
    

		
		
		//Load query
		ReadFile qfile = new ReadFile(query_file);		
		String queryStr = qfile.readFileIntoString();
		
		System.out.println("Query:");
		System.out.println(queryStr);
		
		
		//Execute query and print results
	    Query q = QueryFactory.create(queryStr);
		
	    System.out.println("Results: ");
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, inf_model);
				try {
				ResultSet res = qe.execSelect();
				Iterator<String> it;
				String row;
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					it = soln.varNames();
					row="";
					while (it.hasNext()) {
						RDFNode node = soln.get(it.next());
						row += node.toString() + ", ";						
					}										
					System.out.println(row);			
				}
			    
				} finally {
				qe.close();
				}
	    }
			
	

	public static void main(String[] args) {
		String dataset;
		String query_file;
		String ontology_file = null;
		
		String test;
		test="playground";
		//test="world-cities";
		//test="nobel-prizes"; 
		
		if (test.equals("playground")) {
			//Playground
			dataset = "files/lab7/playground.ttl"; 
			query_file = "files/lab7/query_playground.txt";
			
			//Solution to be added
			//query_file = "files/lab7/solution/query7.1_playground.txt";
			//query_file = "files/lab7/solution/query7.2_playground.txt";
			//query_file = "files/lab7/solution/query7.3_playground.txt";
		}
		else if (test.equals("world-cities")) {
			
			//World cities
			dataset = "files/lab7/worldcities-free-100-task1.ttl";
			ontology_file = "files/lab7/ontology_worldcities.ttl";
			query_file = "files/lab7/query_world-cities.txt";
			
			//Solution to be added
			//query_file = "files/lab7/solution/query7.4_world-cities.txt"; 
		}
		else {
			//Nobel prize		
			ontology_file = "files/lab7/nobel-prize-ontology.rdf";
			dataset= "files/nobelprize_kg.nt";
			query_file = "files/lab7/query_nobel-prize.txt";
			
			//Using federation
			//query_file = "files/lab7/query_nobel-prize-service.txt";
			
			//Solution to be added
			//query_file = "files/lab7/solution/query7.5_nobel-prize.txt";
			//query_file = "files/lab7/solution/query7.6_nobel-prize.txt";
		}		
		
		
		
		try {
			
			new QueryLocalRDFKnowledgeGraphs(ontology_file, dataset, query_file);
			
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
