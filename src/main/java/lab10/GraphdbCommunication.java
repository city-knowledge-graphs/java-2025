package lab10;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import util.ReadFile;



public class GraphdbCommunication {
	
	
	public GraphdbCommunication() {
		
	}
	
	
	public void loadingData(String graphdb_endpoint, String file, String format, boolean platform_windows) throws IOException, InterruptedException {
		
		System.out.println("Uploading file: " + file);
		String curl_command;
		
		
		//String userDirectory = FileSystems.getDefault()
	    //        .getPath("")
	    //       .toAbsolutePath()
	    //        .toString();	        
	    //System.out.println(userDirectory2 + File.separator + file);
		//String full_file_path = userDirectory + File.separator + file;		
		
				
		if (format.equals("trig"))
			//curl_command = "curl '" + graphdb_endpoint + "/statements' -X POST -H \"Content-Type:application/x-trig\" -T '" + full_file_path + "'";
			curl_command = "curl -X POST -H \"Content-Type:application/x-trig\" -T " + file + " " + graphdb_endpoint + "/statements";
		else
			//curl_command = "curl '" + graphdb_endpoint + "/statements' -X POST -H \"Content-Type:application/x-turtle\" -T '" + full_file_path + "'";
			curl_command = "curl -X POST -H \"Content-Type:application/x-turtle\" -T " + file + " " + graphdb_endpoint + "/statements";
	    
	    String[] commands;
	    
	    //Windows
	    if (platform_windows)
	    	commands = new String[]{"CMD", "/c", curl_command};
	    else	    
	    	//Linux/Mac
	    	commands = new String[]{"/bin/bash", "-c", curl_command};
	    
	    
	    System.out.println(commands[0] + " " + commands[1] + " " +  commands[2]);
	    
	    
	    
	    
	    
		Runtime.getRuntime().exec(commands);
		
		
	}
	
	
	public void queryGraphDBRepo(String graphdb_endpoint, String queryStr) {
		
		System.out.println("Querying " + graphdb_endpoint);
		
		
		Query q = QueryFactory.create(queryStr);
		
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(graphdb_endpoint,q);
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
		
				
		String test;		
		
		boolean platform_windows = true; //Set to false for Mac/Linux
		
		
		test="world-cities";
		test="nobel-prizes";
		//test="named-graph";
		//boolean load_Data=true;
		boolean load_Data=false;
		
		
		String graphdb_endpoint;
		String path_to_data_file;
		String path_to_onto_file=null;
		String queryStr;
		String format="ttl";
		String query_file;
		
		String localhost = "http://127.0.0.1:7200";
		
		try {
		
			if (test.equals("world-cities")) {
				//GraphDB Endpoint
				graphdb_endpoint = localhost + "/repositories/lab_graphdb";
				
				path_to_data_file = "files/lab7/worldcities-free-100-task1.ttl";
				path_to_onto_file = "files/lab7/ontology_worldcities.ttl";
				queryStr = 
			            "PREFIX wco: <http://www.semanticweb.org/ernesto/in3067-inm713/wco/>\n" +
			                   	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
			                	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
			                   	"SELECT DISTINCT ?country (COUNT(?city) AS ?num_cities) WHERE { \n" +
			                    "?country wco:hasCity ?city .  \n" +
			                "\n}"+
			                "GROUP BY ?country\n" +
			                "ORDER BY DESC(?num_cities)\n" +
			                "LIMIT 10";
			}
			else if(test.equals("nobel-prizes")) {
				
				//Load query from file				
				//query_file="files/lab10/query_nobel-prize-service.txt";
				//query_file="files/lab10/query7.6_nobel-prize.txt";
				query_file="files/lab7/solution/query7.5_nobel-prize.txt";
				
				ReadFile qfile = new ReadFile(query_file);		
				queryStr = qfile.readFileIntoString();
				
				graphdb_endpoint = localhost + "/repositories/nobel_prize_java";
				path_to_onto_file = "files/lab7/nobel-prize-ontology.rdf";
				path_to_data_file = "files/nobelprize_kg.nt";
				
			}
			else {
				format="trig";
				graphdb_endpoint = localhost + "/repositories/named_graphs_java";    
				path_to_data_file = "files/lab10/named_graphs.ttl";
				
					    
				query_file="files/lab10/query_named_simple.txt";
				//query_file="files/lab10/query_named1.txt";
				//query_file="files/lab10/query_named2.txt";
				//query_file="files/lab10/query_named_all.txt";
				//query_file="files/lab10/query_named_from.txt";
				
					    
				ReadFile qfile = new ReadFile(query_file);		
				queryStr = qfile.readFileIntoString();
				
			}
			
		

		
			GraphdbCommunication graphdb_access = new GraphdbCommunication();
		
			
			//LOAD DATA
			if (load_Data) {
				if (path_to_onto_file!=null) 
					graphdb_access.loadingData(graphdb_endpoint, path_to_onto_file, format, platform_windows);
				
				graphdb_access.loadingData(graphdb_endpoint, path_to_data_file, format, platform_windows);
			}
			
			
			
			//QUERY DATA			
			graphdb_access.queryGraphDBRepo(graphdb_endpoint, queryStr);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
